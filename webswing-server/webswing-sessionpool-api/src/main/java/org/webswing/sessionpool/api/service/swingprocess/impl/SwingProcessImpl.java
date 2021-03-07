package org.webswing.sessionpool.api.service.swingprocess.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.FileSize;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.DeleteAction;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileSize;
import org.apache.logging.log4j.core.appender.rolling.action.IfFileName;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.core.appender.rolling.action.PathSortByModificationTime;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.webswing.Constants;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.util.ServerUtil;
import org.webswing.sessionpool.api.service.swingprocess.ApplicationExitListener;
import org.webswing.sessionpool.api.service.swingprocess.ProcessExitListener;
import org.webswing.sessionpool.api.service.swingprocess.ProcessStatusListener;
import org.webswing.sessionpool.api.service.swingprocess.SwingProcess;
import org.webswing.sessionpool.api.service.swingprocess.SwingProcessConfig;
import org.webswing.sessionpool.api.service.swingprocess.impl.SwingProcessServiceImpl.SessionLogAppenderParams;

public class SwingProcessImpl implements SwingProcess {
	private final ScheduledExecutorService processHandlerThread;
	private static final long LOG_POLLING_PERIOD = 100L;
	private static final long HEARTBEAT_PERIOD = 1000L;
	private static final long DEFAULT_LOG_SIZE = 10 * 1024 * 1024; // 10 MB
	
	private final String instanceId;
	private final SwingProcessConfig config;
	private final SwingConfig swingConfig;
	private Logger defaultLog;
	private Logger log;
	private Process process;
	private ScheduledFuture<?> logsProcessor;
	private ScheduledFuture<?> heartbeat;
	private InputStream out;
	private InputStream err;
	private OutputStream in;
	private StringBuilder bufferOut = new StringBuilder();
	private StringBuilder bufferErr = new StringBuilder();
	private byte[] buffer = new byte[4096];
	private boolean hasSessionLog;
	private String sessionLogDestination;

	private boolean destroying;
	private ScheduledFuture<?> delayedTermination;
	private boolean forceKilled = false;
	private ProcessExitListener closeListener;
	private ProcessStatusListener statusListener;
	private ApplicationExitListener appExitListener;

	public SwingProcessImpl(String instanceId, SwingProcessConfig config, SwingConfig swingConfig, ScheduledExecutorService processHandlerThread) {
		super();
		this.instanceId = instanceId;
		this.config = config;
		this.swingConfig = swingConfig;
		
		defaultLog = (Logger) LogManager.getLogger(SwingProcessImpl.class + "_" + config.getApplicationName());
		log = defaultLog;
		this.processHandlerThread = processHandlerThread;
	}

	public void execute() throws Exception {
		if (!isRunning()) {
			ProcessBuilder processBuilder = new ProcessBuilder(buildCommandline());
			if (verifyBaseDir()) {
				processBuilder.directory(new File(config.getBaseDir()));
			}
			
			List<String> protectedCommand = getProtectedCommand(processBuilder.command());
			log.info("Starting application process [" + config.getName() + "] from [" + config.getBaseDir() + "] :" + protectedCommand);
			
			process = processBuilder.start();
			
			initSessionLog(process);
			
			if (hasSessionLog) {
				log.info("Starting application process [" + config.getName() + "] from [" + config.getBaseDir() + "] :" + protectedCommand); // log same message into session log
				defaultLog.info("Logging into: " + sessionLogDestination);
				log.info("Logging into: " + sessionLogDestination);
			}
			
			if (statusListener != null) {
				statusListener.statusChanged();
			}
			
			logsProcessor = processHandlerThread.scheduleAtFixedRate(() -> {
				if (process != null) {
					if (out == null || err == null) {
						out = process.getInputStream();
						err = process.getErrorStream();
					}
					try {
						processStream(out, bufferOut, buffer, config.getName(), false);
						processStream(err, bufferErr, buffer, config.getName(), true);
					} catch (Exception e) {
						log.error("Failed to process process logs for application process " + config.getName(), e);
						destroy();
					}
					if (!SwingProcessImpl.this.isRunning()) {
						destroy();
					}
				}
			}, LOG_POLLING_PERIOD, LOG_POLLING_PERIOD, TimeUnit.MILLISECONDS);
			
			heartbeat = processHandlerThread.scheduleAtFixedRate(() -> {
				if (process != null) {
					if (in == null) {
						in = process.getOutputStream();
					}
					try {
						sendHeartbeat(in);
					} catch (Exception e) {
						log.error("Failed to send heartbeat ping to application process " + config.getName(), e);
					}
				}
			}, HEARTBEAT_PERIOD, HEARTBEAT_PERIOD, TimeUnit.MILLISECONDS);
		} else {
			throw new IllegalStateException("Process is already running.");
		}
	}
	
	private List<String> getProtectedCommand(List<String> command) {
		List<String> protectedCommand = new ArrayList<>(command.size());
		
		command.forEach(c -> {
			if (c.startsWith("-Dwebswing.connection.secret=")) {
				protectedCommand.add("-Dwebswing.connection.secret=<hidden>");
			} else {
				protectedCommand.add(c);
			}
		});
		
		return protectedCommand;
	}

	private boolean verifyBaseDir() {
		if (config.getBaseDir() == null || config.getBaseDir().isEmpty()) {
			return false;
		} else {
			File file = new File(config.getBaseDir());
			if (file.exists() && file.isDirectory() && file.canRead()) {
				return true;
			} else {
				String error = "";
				if (!file.exists()) {
					error = "Path does not exist.";
				} else if (!file.isDirectory()) {
					error = "Path is not a directory";
				} else if (!file.canRead()) {
					error = "Directory is not accessible";
				}
				throw new IllegalArgumentException("Failed to start application process with base dir:'" + config.getBaseDir() + "'. " + error);
			}
		}
	}

	public void destroy() {
		destroy(0);
	}

	public void destroy(int delayMs) {
		if (delayMs > 0 && delayedTermination == null) {
			log.info("Waiting " + delayMs + "ms for app process " + config.getName() + " to end.");
			delayedTermination = processHandlerThread.schedule(new Runnable() {
				@Override
				public void run() {
					destroy(0);
				}
			}, delayMs, TimeUnit.MILLISECONDS);
		} else if (!destroying) {
			destroying = true;
			try {
				if (delayedTermination != null) {
					delayedTermination.cancel(false);
				}
				destroyInternal();
			} finally {
				logsProcessor.cancel(false);
				heartbeat.cancel(false);
				log.info("[" + config.getName() + "] app process terminated. ");
				if (hasSessionLog) {
					defaultLog.info("[" + config.getName() + "] app process terminated. ");
				}
				if (closeListener != null) {
					try {
						closeListener.onClose();
					} catch (Exception e) {
						log.error("Failed to call onClose", e);
					}
				}
				if (hasSessionLog) {
					log.getAppenders().values().forEach(appender -> appender.stop());
				}
				destroying = false;
			}
		}
	}

	private void destroyInternal() {
		if (isRunning()) {
			log.error("Killing Application process " + config.getName() + ".");
			process.destroy();
			forceKilled = true;
		}
		if (statusListener != null) {
			statusListener.statusChanged();
		}
	}

	public boolean isRunning() {
		if (process == null) {
			return false;
		}
		try {
			process.exitValue();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	private String[] buildCommandline() throws Exception {
		List<String> cmd = new ArrayList<String>();

		if (config.getJreExecutable() == null || config.getJreExecutable().isEmpty()) {
			throw new IllegalArgumentException("JRE executable cannot be empty. Please specify JRE.");
		}
		translateAndAdd(cmd, config.getJreExecutable(), "jreExecutable");
		if (config.getJvmArgs() != null) {
			translateAndAdd(cmd, config.getJvmArgs(), "jvmArgs");
		}
		if (config.getProperties().size() > 0) {
			for (Entry<String, String> entry : config.getProperties().entrySet()) {
				String property = "-D" + entry.getKey();
				String value = entry.getValue();
				if (value != null && !value.isEmpty()) {
					property += "=" + value;
				}
				cmd.add(property);
			}
		}
		if (config.getClassPath() != null) {
			cmd.add("-cp");
			cmd.add(config.getClassPath());
		}
		if (config.getMainClass() != null) {
			cmd.add(config.getMainClass());
		}
		if (config.getArgs() != null) {
			translateAndAdd(cmd, config.getArgs(), "args");
		}
		return cmd.toArray(new String[cmd.size()]);
	}

	private void translateAndAdd(List<String> cmd, String args, String fieldName) throws Exception {
		try {
			for (String s : translateCommandline(args)) {
				cmd.add(s);
			}
		} catch (Exception e) {
			throw new Exception("Illegal value for '" + fieldName + "' field.", e);
		}
	}

	/**
	 * Copy of method from Apache Ant - Commandline class. Crack a command line.
	 * 
	 * @param toProcess
	 *            the command line to process.
	 * @return the command line broken into strings. An empty or null toProcess
	 *         parameter results in a zero sized array.
	 * @throws Exception
	 */
	public static String[] translateCommandline(String toProcess) throws Exception {
		if (toProcess == null || toProcess.length() == 0) {
			// no command? no string
			return new String[0];
		}
		// parse with a simple finite state machine

		final int normal = 0;
		final int inQuote = 1;
		final int inDoubleQuote = 2;
		int state = normal;
		final StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
		final ArrayList<String> result = new ArrayList<String>();
		final StringBuilder current = new StringBuilder();
		boolean lastTokenHasBeenQuoted = false;

		while (tok.hasMoreTokens()) {
			String nextTok = tok.nextToken();
			switch (state) {
			case inQuote:
				if ("\'".equals(nextTok)) {
					lastTokenHasBeenQuoted = true;
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			case inDoubleQuote:
				if ("\"".equals(nextTok)) {
					lastTokenHasBeenQuoted = true;
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			default:
				if ("\'".equals(nextTok)) {
					state = inQuote;
				} else if ("\"".equals(nextTok)) {
					state = inDoubleQuote;
				} else if (" ".equals(nextTok)) {
					if (lastTokenHasBeenQuoted || current.length() != 0) {
						result.add(current.toString());
						current.setLength(0);
					}
				} else {
					current.append(nextTok);
				}
				lastTokenHasBeenQuoted = false;
				break;
			}
		}
		if (lastTokenHasBeenQuoted || current.length() != 0) {
			result.add(current.toString());
		}
		if (state == inQuote || state == inDoubleQuote) {
			throw new Exception("unbalanced quotes in " + toProcess);
		}
		return result.toArray(new String[result.size()]);
	}

	private void processStream(InputStream out, StringBuilder bufferOut, byte[] buffer, String name, boolean isError) throws IOException {
		long start = System.currentTimeMillis();
		boolean timeout = false;
		while (out.available() > 0 && !timeout) {
			int available = out.available();
			int read = out.read(buffer, 0, available > buffer.length ? buffer.length : available);
			bufferOut.append(new String(buffer, 0, read));
			while (bufferOut.indexOf("\n") >= 0) {
				int indexofNewLine = bufferOut.indexOf("\n");
				boolean isCR = indexofNewLine > 0 && bufferOut.charAt(indexofNewLine - 1) == '\r';
				String appMsg = bufferOut.subSequence(0, isCR ? indexofNewLine - 1 : indexofNewLine).toString();
				
				if (!handleSystemMessage(appMsg)) {
					String msg = "[" + name + "] " + appMsg;
					if (isError) {
						log.error(msg);
					} else {
						log.info(msg);
					}
				}
				
				bufferOut.delete(0, indexofNewLine + 1);
			}
			timeout = System.currentTimeMillis() - start > LOG_POLLING_PERIOD ? true : false;
		}
	}
	
	private boolean handleSystemMessage(String appMsg) {
		if (!appMsg.startsWith(Constants.APP_LOGGER_SYSTEM_MSG_PREFIX)) {
			return false;
		}
		
		String[] sysMsgSplit = appMsg.split(" ", 2);
		if (sysMsgSplit.length != 2) {
			return false;
		}
		
		String systemMsg = sysMsgSplit[1];
		
		if (Constants.APP_LOGGER_SYSTEM_MSG_EXIT.equals(systemMsg)) {
			if (appExitListener != null) {
				appExitListener.onExit();
			}
			return true;
		}
		
		return false;
	}
	
	private void sendHeartbeat(OutputStream in) throws IOException {
		in.write("ping\r\n".getBytes(StandardCharsets.UTF_8));
		in.flush();
	}
	
	@Override
	public void reconnect(String serverUrl) {
		if (process == null || in == null) {
			log.error("Cannot send reconnect to server, process or input stream is null!");
			return;
		}
		
		try {
			in.write(new String("reconnect " + serverUrl + "\r\n").getBytes(StandardCharsets.UTF_8));
			in.flush();
		} catch (Exception e) {
			log.error("Failed to send reconnect to application process " + config.getName(), e);
		}
	}
	
	private void initSessionLog(Process process) {
		if (config.getSessionLogAppenderParams() == null || process == null) {
			return;
		}
		
		String pid = getProcessPid(process);
		
		log = (Logger) LogManager.getLogger(SwingProcessImpl.class + "_" + config.getApplicationName() + "_" + config.getName()); // because of different log configurations per app, we need a separate logger instance for each app session

		Configuration loggerConfig = ((LoggerContext) LogManager.getContext(false)).getConfiguration();
		loggerConfig.setLoggerAdditive(log, false);
		
		Appender logAppender = createSessionLogAppender(config.getSessionLogAppenderParams(), pid);
		
		if (logAppender instanceof RollingFileAppender) {
			sessionLogDestination = new File(((RollingFileAppender) logAppender).getFileName()).getAbsolutePath();
		}
		
		log.addAppender(logAppender);
		hasSessionLog = true;
	}
	
	private String getProcessPid(Process process) {
		try {
			Method pidMethod = Process.class.getDeclaredMethod("pid");
			if (pidMethod != null) {
				Object result = pidMethod.invoke(process);
				if (result != null) {
					return result.toString();
				}
			}
		} catch (Exception e) {
			log.debug("Could not get PID for process!", e);
		}
		
		return null;
	}
	
	private Appender createSessionLogAppender(SessionLogAppenderParams params, String pid) {
		String logDir = params.sessionLogDir;

		if (StringUtils.isNotBlank(logDir)) {
			// make path relative for logger
			logDir = new File("").toURI().relativize(new File(logDir).toURI()).getPath();
		}

		String appUrlNormalized = ServerUtil.normalizeForFileName(params.pathMapping);
		String instanceIdNormalized = ServerUtil.normalizeForFileName(params.instanceId);
		String logFileName = logDir + "/webswing-" + instanceIdNormalized + "-" + appUrlNormalized + (StringUtils.isNotBlank(pid) ? ("-" + pid) : "") + ".session.log";
		String globPattern = "webswing-*-" + appUrlNormalized + (StringUtils.isNotBlank(pid) ? "-*-" : "") + ".session.log*";

		BuiltConfiguration logConfig = ConfigurationBuilderFactory.newConfigurationBuilder().build();

		long maxLogRollingSize = FileSize.parse(params.singleSize, DEFAULT_LOG_SIZE) / 2;
		SizeBasedTriggeringPolicy sizeBasedPolicy = SizeBasedTriggeringPolicy.createPolicy(maxLogRollingSize + " B");
		String maxSize = params.maxSize;

		RollingFileAppender appender = RollingFileAppender.newBuilder()
				.withName(SwingProcessImpl.class.getName())
				.withFileName(logFileName)
				.withFilePattern(logFileName + ".%i")
				.withAppend(true)
				.withLayout(PatternLayout.newBuilder().withPattern(Constants.SESSION_LOG_PATTERN).build())
				.withPolicy(sizeBasedPolicy)
				.withStrategy(
						DefaultRolloverStrategy.newBuilder()
							.withMax("1")
							.withConfig(logConfig)
							.withCustomActions(new Action[] { 
									DeleteAction.createDeleteAction(logDir, false, 1, false, PathSortByModificationTime.createSorter(true), 
											new PathCondition[] { 
													IfFileName.createNameCondition(globPattern, null, IfAccumulatedFileSize.createFileSizeCondition(maxSize))
											}, null, logConfig)
									})
							.build())
				.build();
		appender.start();

		return appender;
	}

	@Override
	public boolean isForceKilled() {
		return forceKilled;
	}

	@Override
	public void setProcessExitListener(ProcessExitListener closeListener) {
		this.closeListener = closeListener;
	}
	
	@Override
	public void setProcessStatusListener(ProcessStatusListener listener) {
		this.statusListener = listener;
	}
	
	@Override
	public void setApplicationExitListener(ApplicationExitListener listener) {
		this.appExitListener = listener;
	}

	@Override
	public SwingProcessConfig getConfig() {
		return config;
	}

	@Override
	public SwingConfig getSwingConfig() {
		return swingConfig;
	}

	@Override
	public String getInstanceId() {
		return instanceId;
	}
	
}
