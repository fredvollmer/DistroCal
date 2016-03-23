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
import java.util.HashSet;
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
    private final int httpPort = 1024;

    Thread socketThread;

    private Set<Node> otherNodes;
    private Node thisNode;
    private TimeMatrix timeMatrix;

    /*
     Constructor
     Instantiates server socket
     */
    public DistroCal(String[] IPs, String thisIP) throws IOException {
        otherNodes = new HashSet<> ();

        // Create "this" node
        thisNode = new Node(thisIP);
        
        // Create remote Nodes
        for (String ip : IPs) {
            otherNodes.add(new Node(ip));
        }
        
        // Create 2D time table
        // Build set of ALL nodes (includes this node)
        Set<Node> allNodes = new HashSet<> ();
        allNodes.addAll(DistroCal.getInstance().getOtherNodes());
        allNodes.add(DistroCal.getInstance().getThisNode());
        
        timeMatrix = new TimeMatrix(allNodes);

        // Start listener thread
        socketThread = new SocketThread(thisNode.getPort());
        socketThread.start();

        // start HTTP server
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
        [0]     --> [IP]:[Port] of THIS node
        [1...n] --> [IP]:[Port] of remote Nodes
         */

        // Create DistroCal
        String[] IPs = Arrays.copyOfRange(args, 1, args.length);
        try {
            instance = new DistroCal(IPs, args[0]);
        } catch (IOException ex) {
            Logger.getLogger(DistroCal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static DistroCal getInstance() {
        return DistroCal.instance;
    }
    
    public TimeMatrix getTimeMatrix() {
        return timeMatrix;
    }
    
    public Set<Node> getOtherNodes() {
        return otherNodes;
    }
    
    public Node getThisNode() {
        return thisNode;
    }
    
    public Set<Event> getEvents () {
        return events;
    }
}
