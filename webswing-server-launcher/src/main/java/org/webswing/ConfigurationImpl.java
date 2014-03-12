package org.webswing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ConfigurationImpl extends Configuration {

    private String host = "localhost";
    private String port = "8080";

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

    public static Configuration parse(String[] args) {
        ConfigurationImpl cimpl = (ConfigurationImpl) Configuration.getInstance();
        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption("h", "host", true, "Local interface address where the web server will listen.(localhost)");
        options.addOption("p", "port", true, "Port where the web server will listen.(8080)");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue('h') != null) {
                cimpl.setHost(line.getOptionValue('h'));
            }
            if (line.getOptionValue('p') != null) {
                cimpl.setPort(line.getOptionValue('p'));
            }
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("webswing", options);
        }
        return cimpl;
    }

}
