package org.webswing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.webswing.server.JmsService;
import org.webswing.server.SwingServer;


public class Main {

    public static void main(String[] args) throws Exception {
        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption("h", "host", true, "Local interface address where the web server will listen.(localhost)");
        options.addOption("p", "port", true, "Port where the web server will listen.(8080)");
        options.addOption("c", "clients", true, "Maximum number of simultaneous swing connections.(10)");
        options.addOption("m", "mainClass", true, "Swing application main class.");
        options.addOption("a", "args", true, "Commandline arguments for swing application.");
        options.addOption("v", "vmargs", true, "JVM arguments for swing application.");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            // validate that main class has been set
            if (!line.hasOption("m")) {
                System.out.println("Please specify swing application's main class");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("webswing", options);
            }
            ConfigurationImpl cimpl = (ConfigurationImpl) Configuration.getInstance();
            if (line.getOptionValue('h') != null) {
                cimpl.setHost(line.getOptionValue('h'));
            }
            if (line.getOptionValue('p') != null) {
                cimpl.setPort(line.getOptionValue('p'));
            }
            if (line.getOptionValue('m') != null) {
                cimpl.setMain(line.getOptionValue('m'));
            }
            if (line.getOptionValue('a') != null) {
                cimpl.setArgs(line.getOptionValue('a'));
            }
            if (line.getOptionValue('v') != null) {
                cimpl.setVmargs(line.getOptionValue('v'));
            }
            if (line.getOptionValue('c') != null) {
                cimpl.setClients(Integer.valueOf(line.getOptionValue('c')));
            }
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("webswing", options);
        }

        JmsService.startService();
        SwingServer.startServer();

    }

}
