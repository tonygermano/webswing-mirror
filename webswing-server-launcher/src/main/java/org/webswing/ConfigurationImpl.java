package org.webswing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.webswing.util.Logger;

public class ConfigurationImpl extends Configuration {

	private String host = "localhost";
	private String port = "8080";
	private String configFile;
	private String usersFile;

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	protected void setHost(String host) {
		this.host = host;
	}

	protected void setPort(String port) {
		this.port = port;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getUsersFile() {
		return usersFile;
	}

	public void setUsersFile(String usersFile) {
		this.usersFile = usersFile;
	}

	public static Configuration parse(String[] args) {
		ConfigurationImpl cimpl = (ConfigurationImpl) Configuration.getInstance();
		// create the command line parser
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		options.addOption("h", "host", true, "Local interface address where the web server will listen. (localhost)");
		options.addOption("p", "port", true, "Port where the web server will listen. (8080)");
		options.addOption("t", "temp", true, "The folder where temp folder will be created for the webswing server. (./tmp)");
		options.addOption("d", true, "Create new temp folder for every webswing instance (default = false)");
		options.addOption("c", "config", true, "Configuration file name. (<webswing-server.war path>/webswing.config)");
		options.addOption("u", "users", true, "Users properties file name. (<webswing-server.war path>/users.properties)");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.getOptionValue('h') != null) {
				cimpl.setHost(line.getOptionValue('h'));
			}
			if (line.getOptionValue('p') != null) {
				cimpl.setPort(line.getOptionValue('p'));
			}
			if (line.getOptionValue('c') != null) {
				cimpl.setConfigFile(line.getOptionValue('c'));
			}
			if (line.getOptionValue('u') != null) {
				cimpl.setUsersFile(line.getOptionValue('u'));
			}
			// NOTE: -d and -t are parsed in main.Main
		} catch (ParseException exp) {
			Logger.info(exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("webswing", options);
		}
		return cimpl;
	}

}
