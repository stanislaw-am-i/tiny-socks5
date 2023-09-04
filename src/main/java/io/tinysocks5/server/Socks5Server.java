package io.tinysocks5.server;

import io.tinysocks5.Main;
import io.tinysocks5.client.Socks5Client;
import io.tinysocks5.server.auth.AuthMethod;
import io.tinysocks5.server.auth.NoAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Socks5Server {

    private int port;
    private ServerSocket socket;
    private AuthMethod authenticationMethod;
    private static Logger logger = LoggerFactory.getLogger(Socks5Server.class);

    public Socks5Server(String port) throws IOException {
        this.port = Integer.valueOf(port);
        socket = new ServerSocket(this.port);
    }

    public void start() throws IOException {
        logger.info("Server started on port {}\n", port);

        while (true) {
            Socket clientSocket = socket.accept();
            new Socks5Client(clientSocket, authenticationMethod).start();
        }
    }

    public void setAuthenticationMethods() {
        authenticationMethod = new NoAuthentication();
    }

    public void setAuthenticationMethods(String username, String password) {
        //TODO: auth method with username and password
    }
}
