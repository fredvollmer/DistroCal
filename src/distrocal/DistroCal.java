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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// NOTE: Access this DistroCal instance via DistroCal.getInstance()
/**
 *
 * @author fredvollmer
 */
public class DistroCal {

    private static DistroCal instance;
    public boolean isCrashed = false;

    private ServerSocket serverSocket;
    private final int httpPort = 8000;

    Thread socketThread;

    private Set<Node> otherNodes;
    private Log partialLog;
    // Map [day+hour+address] to event
    private Map<String, Appointment> calendar;
    private Node thisNode;
    private TimeMatrix timeMatrix;

    /*
     Constructor
     Instantiates server socket
     */
    public DistroCal(String[] IPs, String thisIP) throws IOException {
        otherNodes = new HashSet<>();
        calendar = new HashMap<>();

        // Create "this" node
        thisNode = new Node(thisIP);

        // Create remote Nodes
        for (String ip : IPs) {
            otherNodes.add(new Node(ip));
        }

        // Create 2D time table
        // Build set of ALL nodes (includes this node)
        Set<Node> allNodes = new HashSet<>();
        allNodes.addAll(otherNodes);
        allNodes.add(thisNode);

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
        
        // Test appt
        Set<String> testNodes = new HashSet<>();
        for (Node n : otherNodes) {
            testNodes.add(n.getAddress());
        }
        Appointment a = new Appointment("Test appointment", 1, 3, 7, thisNode.getAddress(), testNodes);
        String key = a.getDay() + "-" + a.getStartTime() + thisNode.getAddress();
        calendar.put(key, a);
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

    /*
     Add events from a received log into our own log
     At this point the log will already have been trimmed to only included
     events which are not already in the node's log
     This is for Hunter to tackle
     */
    public void integrateReceivedLog(Log l) {
        // This is the log for this node
        Log myLog = partialLog;
    }

    /*
     Process events from partial log into calendar
     Sends RSVP or deletes when dealing with multi-node appointments
     Does not protect from duplicates--log must be trimmed!
     */
    public void processLogIntoCalendar(Log l) {
        for (Event e : l.getEvents()) {
            Appointment ce = e.getCalendarEvent();
            String key = "";
            
            switch (e.getType()) {
                case INSERT:
                    
                    // Check if this node is part of appointment
                    if (ce.getNodes().contains(instance.thisNode.getAddress())) {
                        // Check if this node is free
                        if (instance.thisNode.isAvailable(ce.getDay(), ce.getStartTime(), ce.getEndTime())) {

                            // Create new appointment and add to calendar
                            Appointment ne = ce.createCopyOfGroupApptForThisNode();
                            key = ce.getDay() + "-" + ce.getStartTime() + instance.thisNode.getAddress();
                            calendar.put(key, ne);

                            // Create RSVP event and add to log
                            Event rsvp = new Event(EventType.RSVP, ce);
                            partialLog.createEvent(rsvp);

                        } else {
                            // Delete appointment by creating DELETE event
                            Event delete = new Event(EventType.DELETE, ce);
                            partialLog.createEvent(delete);
                        }
                    } else {
                        // This event has nothing to do with this node
                        key = ce.getDay() + "-" + ce.getStartTime() + "-" + ce.getCreator();
                        calendar.put(key, ce);
                    }

                    break;

                case DELETE:
                    // Check if this event included this node, delete clone if so
                    if (ce.getNodes().contains(instance.thisNode.getAddress())) {
                        // Delete this node's clone of this appointment
                        key = ce.getDay() + "-" + ce.getStartTime() + instance.thisNode.getAddress();
                        calendar.remove(key);
                    }
                    
                    // Delete event from calendar
                    key = ce.getDay() + "-" + ce.getStartTime() + "-" + ce.getCreator();
                    calendar.remove(key);
                    
                    break;
                    
                case RSVP:
                    // Remove sending node from unconfirmed set for this appoointment
                    // Find this node's clone of group appointment
                    key = ce.getDay() + "-" + ce.getStartTime() + instance.thisNode.getAddress();
                    Appointment a = calendar.get(key);
                    if (a == null) {
                        System.err.println("Error in RSVP: appointment clone not found");
                        return;
                    }
                    a.confirmNode(e.getNode());
            }
        }
    }

    /*
     Imitate node failure
     */
    public static void crash() {
        instance.isCrashed = true;
    }

    /*
     Imitate node recovery
     */
    public static void recover() {
        instance.isCrashed = false;
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

    public Map<String, Appointment> getAppointments() {
        return calendar;
    }
    
    public Set<Appointment>  getAppointmentsAsSet () {
        Set<Appointment> c = new HashSet(calendar.values());
        return c;
    }
}
