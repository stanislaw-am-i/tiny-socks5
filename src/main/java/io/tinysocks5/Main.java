package io.tinysocks5;

import io.tinysocks5.config.ServerConfig;
import io.tinysocks5.server.Socks5Server;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class Main {

    private static final ServerConfig CONFIGURATION = new ServerConfig();
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Options OPTIONS = new Options();

    public static void main(String[] args) {
        processedArguments(args);

        try {
            launchServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void launchServer() throws IOException {
        Socks5Server server = new Socks5Server(CONFIGURATION.getPort());
        if (CONFIGURATION.isAuth()) {
            server.setAuthenticationMethods(CONFIGURATION.getUsername(), CONFIGURATION.getPassword());
        } else {
            server.setAuthenticationMethods();
        }

        server.start();
    }

    private static void processedArguments(String[] args) {
        OPTIONS.addOption("h", "help", false, "Display this help message");
        OPTIONS.addOption("p", "port", true, "Set the port for the server to listen on");
        OPTIONS.addOption("a", "auth", true, "Set authentication credentials in the format 'username:password'");

        CommandLine commandLine;
        CommandLineParser parser = new DefaultParser();

        try {
            commandLine = parser.parse(OPTIONS, args);

            if (commandLine.hasOption("h")) {
                printHelp();
                System.exit(1);
            }

            if (commandLine.hasOption("p")) {
                CONFIGURATION.setPort(commandLine.getOptionValue("p"));
            }

            if (commandLine.hasOption("a")) {
                String authString = commandLine.getOptionValue("a");
                parseAuthCredentials(authString);
                CONFIGURATION.setAuth(true);
            } else {
                CONFIGURATION.setAuth(false);
            }
        } catch (ParseException e) {
            LOGGER.error("{}\n", "Error parsing command line: " + e.getMessage());
        }
    }

    private static void parseAuthCredentials(String authString) {
        boolean isValid = authString != null && !"".equals(authString) && authString.indexOf(':') != -1;
        if (isValid) {
            CONFIGURATION.setUsername(authString.substring(0, authString.indexOf(':')));
            CONFIGURATION.setPassword(authString.substring(authString.indexOf(':') + 1));
        } else {
            LOGGER.error("{}\n", "Error parsing auth credentials");
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar tiny-socks5.jar", OPTIONS);
    }
}