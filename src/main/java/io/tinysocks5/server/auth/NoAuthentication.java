package io.tinysocks5.server.auth;

import java.net.Socket;

public class NoAuthentication implements AuthMethod {

    @Override
    public byte getCode() {
        return 0;
    }

    @Override
    public boolean auth(Socket clientSocket) {
        return true;
    }
}
