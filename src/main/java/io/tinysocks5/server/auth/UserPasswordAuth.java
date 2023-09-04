package io.tinysocks5.server.auth;

public class UserPasswordAuth implements AuthMethod {

    private final String username;
    private final String password;

    public UserPasswordAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public byte getCode() {
        return 2;
    }

    @Override
    public boolean validateUsernamePassword(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
