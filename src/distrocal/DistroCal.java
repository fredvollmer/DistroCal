/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import com.sun.net.httpserver.HttpServer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredvollmer
 */
public class DistroCal {

    private static DistroCal instance;
    public boolean isCrashed = false;

    private ServerSocket serverSocket;
    private int socketPort = 3000;
    private int httpPort = 80;

    Thread socketThread;
    Thread httpThread;

    private Set<Node> nodes;

    /*
     Constructor
     Instantiates server socket
     */
    public DistroCal(String[] IPs, int port) throws IOException {
        socketPort = port;

        serverSocket.setSoTimeout(10000);

        // Create "this" node
        // Create remote Nodes
        for (String ip : IPs) {
            nodes.add(new Node(ip, socketPort));
        }

        // Start listener threads
        socketThread = new SocketThread(socketPort);
        socketThread.start();

        HttpServer server = HttpServer.create(new InetSocketAddress(httpPort), 200);
        server.createContext("/", new RequestHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("DistroCal node started...");
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

        // Create DistroCal
        String[] IPs = Arrays.copyOfRange(args, 1, args.length);
        try {
            instance = new DistroCal(IPs, Integer.parseInt(args[0]));
            System.out.println("DistroCal initialized");
        } catch (IOException ex) {
            Logger.getLogger(DistroCal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static DistroCal getInstance() {
        return DistroCal.instance;
    }

}
