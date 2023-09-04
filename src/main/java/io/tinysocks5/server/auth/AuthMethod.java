package io.tinysocks5.server.auth;

import java.net.Socket;

public interface AuthMethod {

    byte getCode();
    boolean auth(Socket clientSocket);
}
