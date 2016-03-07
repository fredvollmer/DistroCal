/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredvollmer
 */
public class DistroCal extends Thread {

    private ServerSocket serverSocket;
    private int listeningPort = 3000;
    
    private Set<Node> nodes;

    /*
     Constructor
     Instantiates server socket
     */
    public DistroCal(int ID, String[] IPs, int port) throws IOException {
        listeningPort = port;
        
        serverSocket = new ServerSocket(listeningPort);
        serverSocket.setSoTimeout(10000);
        System.out.println("DistroCal node started...");
        
        // Create "this" node
        
        // Create remote Nodes
        for (String ip : IPs) {
             nodes.add(new Node(ip, listeningPort));
        }
    }

    /*
     Run thread
     */
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
        /**
         * @param args the command line arguments
         */
    public static void main(String[] args) {
        /*
        Parse arguments
        [0]     --> Port to connect to remote Nodes with and listen on
        [1...n] --> IP addresses of remote Nodes
        */
        
        // Create "this" Node
        
        
    }

}
