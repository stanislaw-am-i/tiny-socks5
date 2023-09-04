package io.tinysocks5.client;

import io.tinysocks5.response.ResponseHandler;
import io.tinysocks5.server.auth.AuthMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class Socks5Client extends Thread {

    private final Socket clientSocket;
    private static final Logger LOGGER = LoggerFactory.getLogger(Socks5Client.class);
    private DataInputStream in;
    private DataOutputStream out;
    private final AuthMethod authenticationMethod;

    public Socks5Client(Socket clientSocket, AuthMethod authenticationMethod) {
        this.clientSocket = clientSocket;
        this.authenticationMethod = authenticationMethod;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(this.clientSocket.getInputStream());
            out = new DataOutputStream(this.clientSocket.getOutputStream());

            // Check SOCKS5 protocol version and supported authentication methods
            if (!validateSocks5ProtocolVersion()) {
                closeClientSocket();
                return;
            }

            if (!selectAuthenticationMethod()) {
                closeClientSocket();
                return;
            }

            // Handle SOCKS5 request
            ResponseHandler request = readSocks5Request();

            // Connect to the target server
            Socket targetSocket = connectToTargetServer(request.getAddress(), request.getPort());

            // Respond with success and establish tunnel
            out.write(response(0, targetSocket.getLocalAddress(), targetSocket.getPort()));

            // Transfer data between client and server
            tunnelData(clientSocket, targetSocket);

            // Close the sockets
            closeSockets(clientSocket, targetSocket);

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            LOGGER.error("{}\n", "Error trying to establish connection: " + sw);
        }
    }

    private boolean validateSocks5ProtocolVersion() throws IOException {
        int version = in.readByte();
        return version == 5;
    }

    private boolean selectAuthenticationMethod() throws IOException {
        int numMethods = in.readByte();
        byte[] methods = in.readNBytes(numMethods);

        int currentAuthMethod = authenticationMethod.getCode();
        for (byte code : methods) {
            if (currentAuthMethod == code) {
                return doAuth(currentAuthMethod);
            }
        }

        out.write(new byte[]{5, (byte) 0xFF}); // No acceptable authentication methods
        return false;
    }

    private boolean doAuth(int code) throws IOException {
        switch (code) {
            case 0 -> {
                out.write(new byte[]{5, 0});
                return true;
            }
            case 2 -> {
                out.write(new byte[]{5, 2});
                return authenticateUsernamePassword();
            }
            default -> {
                return false;
            }
        }
    }

    private boolean authenticateUsernamePassword() throws IOException {
        int currentAuthenticationVersion  = in.readByte(); // 0x01 for current version of username/password authentication
        if (currentAuthenticationVersion != 1) {
            return false;
        }

        int usernameLength = in.readByte();
        byte[] usernameBytes = in.readNBytes(usernameLength);
        String username = new String(usernameBytes);

        int passwordLength = in.readByte();
        byte[] passwordBytes = in.readNBytes(passwordLength);
        String password = new String(passwordBytes);

        // Validate username and password
        boolean isValid = authenticationMethod.validateUsernamePassword(username, password);

        if (isValid) {
            out.write(new byte[]{1, 0}); // Authentication successful
        } else {
            out.write(new byte[]{1, 1}); // Authentication failed
        }

        return isValid;
    }

    private ResponseHandler readSocks5Request() throws IOException {
        // Read the SOCKS5 request
        int version = in.readByte();
        int cmd = in.readByte();
        int reserved = in.readByte();
        int addressType = in.readByte();
        InetAddress address = readAddress(in, addressType);
        int targetPort = in.readUnsignedShort();

        return new ResponseHandler(version, cmd, reserved, address, targetPort);
    }

    private InetAddress readAddress(DataInputStream in, int addressType) throws IOException {
        InetAddress address = null;
        if (addressType == 1) { // IPv4 address
            address = Inet4Address.getByAddress(in.readNBytes(4));
        } else if (addressType == 3) { // Domain name
            String domainName = new String(in.readNBytes(in.readByte()));
            address = InetAddress.getByName(domainName);
        } else if (addressType == 4) { // IPv6 address
            address = Inet6Address.getByAddress(in.readNBytes(16));
        }
        return address;
    }

    private Socket connectToTargetServer(InetAddress address, int port) throws IOException {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            response(4, address, port);
            closeClientSocket();
        }
        return socket;
    }

    private void tunnelData(Socket sourceSocket, Socket targetSocket) throws IOException {
        // Transfer data between client and server
        String hostname = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        LOGGER.info("{} connected\n", hostname);

        while (true) {
            if (!sourceSocket.isConnected()) {
                if (targetSocket.isConnected()) targetSocket.close();
                break;
            }
            if (!targetSocket.isConnected()) {
                if (sourceSocket.isConnected()) sourceSocket.close();
                break;
            }
            DataInputStream sourceInput = new DataInputStream(sourceSocket.getInputStream());
            DataOutputStream targetOutput = new DataOutputStream(targetSocket.getOutputStream());

            if (sourceInput.available() != 0) targetOutput.write(sourceInput.readNBytes(sourceInput.available()));

            DataInputStream targetInput = new DataInputStream(targetSocket.getInputStream());
            DataOutputStream sourceOutput = new DataOutputStream(sourceSocket.getOutputStream());

            if (targetInput.available() != 0) sourceOutput.write(targetInput.readNBytes(targetInput.available()));
        }
        LOGGER.info("{} disconnected\n", hostname);
    }

    private void closeClientSocket() throws IOException {
        clientSocket.close();
    }

    private void closeSockets(Socket... sockets) throws IOException {
        for (Socket socket : sockets) {
            if (socket.isConnected()) {
                socket.close();
            }
        }
    }

    private byte[] response(int status, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(bytes);
        packet.writeByte(5);
        packet.writeByte(status);
        packet.writeByte(0);
        byte[] addressBytes = address.getAddress();
        if (addressBytes.length == 4) {
            packet.writeByte(1);
        } else if (addressBytes.length == 16) {
            packet.writeByte(4);
        } else if (addressBytes.length == 1) {
            packet.writeByte(3);
        }
        packet.write(addressBytes);
        packet.writeShort(port);
        return bytes.toByteArray();
    }
}
