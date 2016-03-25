/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distrocal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author s65q479
 */
public class Log implements Serializable {
    private Set<Event> events;          // Set of events
    private int lastUpdate = -1;        // Time 
    
    public Log () {
        events = new HashSet<>();
    }
    
    public void add (Event e) {
        events.add(e);
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
    
    public Set<Event> getEvents () {
        return events;
    }
}
