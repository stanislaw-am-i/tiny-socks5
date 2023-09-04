package io.tinysocks5.server;

import io.tinysocks5.client.Socks5Client;
import io.tinysocks5.server.auth.AuthMethod;
import io.tinysocks5.server.auth.NoAuth;
import io.tinysocks5.server.auth.UserPasswordAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Socks5Server {

    private final int port;
    private final ServerSocket socket;
    private AuthMethod authenticationMethod;
    private final static Logger LOGGER = LoggerFactory.getLogger(Socks5Server.class);

    public Socks5Server(String port) throws IOException {
        this.port = Integer.parseInt(port);
        socket = new ServerSocket(this.port);
    }

    public void start() throws IOException {
        LOGGER.info("Server started on port {}\n", port);

        while (true) {
            Socket clientSocket = socket.accept();
            new Socks5Client(clientSocket, authenticationMethod).start();
        }
    }

    public void setAuthenticationMethods() {
        authenticationMethod = new NoAuth();
    }

    public void setAuthenticationMethods(String username, String password) {
        authenticationMethod = new UserPasswordAuth(username, password);
    }
}
