package io.tinysocks5.response;

import java.net.InetAddress;

public class ResponseHandler {
    private int version;
    private int cmd;
    private int reserved;
    private InetAddress address;
    private int port;

    public ResponseHandler(int version, int cmd, int reserved, InetAddress address, int port) {
        this.version = version;
        this.cmd = cmd;
        this.reserved = reserved;
        this.address = address;
        this.port = port;
    }

    public int getVersion() {
        return version;
    }

    public int getCmd() {
        return cmd;
    }

    public int getReserved() {
        return reserved;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
