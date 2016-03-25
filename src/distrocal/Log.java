/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distrocal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author s65q479
 */
public class Log implements Serializable {
    private List<Event> events;          // Set of events
    private int lastUpdate = -1;        // Time 
    
    public Log () {
        events = new ArrayList<>();
    }
    
    /*
    Inserts an event into the log, but does no broadcasting
    */
    public void add (Event e) {
        events.add(e);
    }
    
    /*
    Inserts a new event into log and broadcasts to other nodes
    Assumes this event is being created at THIS node
    */
    public void createEvent (Event e) {
        // Increment clock
        DistroCal.getInstance().getTimeMatrix().incrementClock();
        
        // Add event record
        events.add(e);
        
        // Send message to every other node
        for (Node n : DistroCal.getInstance().getOtherNodes()) {
            // Build customized partial log
            Log l = n.createPartialLog();
            // Create message
            Message m = new Message(l, DistroCal.getInstance().getTimeMatrix());
            // Send message
            try {
                n.send(m);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /*
    Removes events from a log which we already know about, based on time matrix
    */
    public void trim () {
        TimeMatrix m = DistroCal.getInstance().getTimeMatrix();
        Node thisNode = DistroCal.getInstance().getThisNode();
        // For each event in partial log, check if we already know about it
        for (Iterator<Event> i = events.iterator(); i.hasNext();) {
            Event e = i.next();
            // If the time this event ocurred is less than what time we have
            // for that node, we can remove it
            int t = m.get(thisNode, e.getNode());
            if (e.getTime() < t) {
                i.remove();
            }
        }
    }
    
    public List<Event> getEvents () {
        return events;
    }
}
