package io.tinysocks5;

import io.tinysocks5.config.ServerConfig;
import io.tinysocks5.server.Socks5Server;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class Main {

    private static ServerConfig configuration = new ServerConfig();
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Options options = new Options();
    private static CommandLine commandLine;

    public static void main(String[] args) {
        processedArguments(args);

        try {
            launchServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void launchServer() throws IOException {
        Socks5Server server = new Socks5Server(configuration.getPort());
        if (configuration.isAuth()) {
            //TODO: auth method with username and password
        } else {
            server.setAuthenticationMethods();
        }

        server.start();
    }

    private static void processedArguments(String[] args) {
        options.addOption("h", "help", false, "Print help message"); //TODO help message
        options.addOption("p", "port", true, "Set the server's port");

        CommandLineParser parser = new DefaultParser();

        try {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("p")) {
                configuration.setPort(commandLine.getOptionValue("p"));
            }

            if (commandLine.hasOption("a")) {
                //TODO: auth method with username and password
            } else {
                configuration.setAuth(false);
            }
        } catch (ParseException e) {
            logger.error("{}\n", "Error parsing command line: " + e.getMessage());
        }
    }
}