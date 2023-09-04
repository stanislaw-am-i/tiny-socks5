package io.tinysocks5.server.auth;

public class NoAuth implements AuthMethod {

    @Override
    public byte getCode() {
        return 0;
    }

    @Override
    public boolean validateUsernamePassword(String username, String password) {
        return false;
    }
}
