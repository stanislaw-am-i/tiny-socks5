package io.tinysocks5.config;

public class ServerConfig {

    private String port;
    private String username;
    private String password;
    private boolean isAuth;

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public boolean isAuth() {
        return isAuth;
    }
}
