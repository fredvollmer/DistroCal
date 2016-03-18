/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author anniefischer
 */
public class SocketThread extends Thread {

    ServerSocket serverSocket;

    public SocketThread(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Listening for message on port "
                        + serverSocket.getLocalPort());
                // Create socket upon connection
                Socket server = serverSocket.accept();
                System.out.println("Socket connection received from "
                        + server.getRemoteSocketAddress());

                // Receive message
                DataInputStream in = new DataInputStream(server.getInputStream());
                ObjectInputStream messageObject = new ObjectInputStream(in);
                Message m = (Message) messageObject.readObject();

                // Handle message
                Message.handleReceivedMessage(m);

            } catch (IOException ex) {
                System.err.println("An error ocurred receiving remote message: ");
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                System.err.println("An error ocurred while parsing message "
                        + "into object.");
                ex.printStackTrace();
            }
        }
    }
}