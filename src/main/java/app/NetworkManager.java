package app;

import app.client.Authenticator;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkManager {

    int port;
    InetAddress address;
    String username;
    String password;

    public NetworkManager(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CommandResponse sendRequest(CommandRequest request) {
        try (Socket socket = new Socket(address, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            request.setUsername(username);
            request.setPassword(password);


            out.writeObject(request);
            out.flush();

            CommandResponse response = (CommandResponse) in.readObject();
            return response;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
