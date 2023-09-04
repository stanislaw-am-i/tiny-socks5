package io.tinysocks5.server.auth;

public interface AuthMethod {

    byte getCode();

    boolean validateUsernamePassword(String username, String password);
}
