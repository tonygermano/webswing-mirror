package org.webswing.server.common.service.url.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.common.service.url.WebSocketUrlLoader;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import main.Main;

public class ScriptWebSocketUrlLoader implements WebSocketUrlLoader {
	
	private static final Logger log = LoggerFactory.getLogger(ScriptWebSocketUrlLoader.class);
	
	private static final long PROCESS_TIMEOUT_SECONDS = 5;
	
	private String scriptFilePath;
	private Set<String> webSocketUrls = Collections.synchronizedSet(new HashSet<>());
	
	private Process process;
	
	public ScriptWebSocketUrlLoader(String scriptFilePath) {
		this.scriptFilePath = getValidFilePath(scriptFilePath);
	}

	@Override
	public void initialize() {
	}

	@Override
	public Set<String> reload() {
		loadFromScript();
		
		synchronized (webSocketUrls) {
			webSocketUrls.clear();
			String urls = System.getProperty(Constants.WEBSWING_SERVER_WEBSOCKET_URL, "");
			Splitter.on(',').trimResults().omitEmptyStrings().split(urls).forEach(url -> {
				if (url.endsWith("/")) {
					url = url.substring(0, url.length() - 1);
				}
				webSocketUrls.add(url);
			});
			return webSocketUrls;
		}
	}
	
	private void loadFromScript() {
		if (scriptFilePath == null) {
			log.warn("Could not reload web socket URLs, no script path defined!");
			return;
		}
		
		if (isRunning()) {
			log.warn("Could not reload web socket URLs, process still running!");
			return;
		}
		
		String urls = "";
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(scriptFilePath);
			process = processBuilder.start();
			
			StringBuilder sb = new StringBuilder();
			
			new Thread(() -> {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = in.readLine()) != null) {
					    sb.append(line);
					}
				} catch (IOException e) {
					log.error("Error while getting websocket URL laoder script output!", e);
				}
			}).start();
			
			process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			
			String output = sb.toString();
			log.debug("Websocket URL loader script output [" + output + "]");
			
			if (StringUtils.isNotBlank(output)) {
				List<String> parsed = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(output);
				if (!parsed.isEmpty()) {
					urls = Joiner.on(',').join(parsed);
				}
			}
		} catch (Exception e) {
			log.error("Error while executing websocket url loader script!", e);
		}
		
		System.setProperty(Constants.WEBSWING_SERVER_WEBSOCKET_URL, urls);
	}
	
	private boolean isRunning() {
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
	
	private String getValidFilePath(String pathOrUri) {
		if (pathOrUri != null) {
			try {
				URI uri = URI.create(pathOrUri);
				File file = new File(uri);
				if (file.exists()) {
					return file.getAbsolutePath();
				} else {
					throw new FileNotFoundException("File " + uri.toString() + "not found.");
				}
			} catch (Exception e) {
				File relativeConfigFile = new File(Main.getConfigProfileDir(), pathOrUri);
				File absoluteConfigFile = new File(pathOrUri);
				if (relativeConfigFile.exists()) {
					return relativeConfigFile.getAbsolutePath();
				} else if (absoluteConfigFile.exists()) {
					return absoluteConfigFile.getAbsolutePath();
				}
			}
		}
		
		return pathOrUri;
	}

}
