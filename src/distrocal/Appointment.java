/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author fredvollmer
 */
public class Appointment implements Serializable {
    private String name;                // Name of the event
    private int day;                    // Integer representation of day of week.
                                        // (Sunday = 0; Saturday = 6)
    private int startTime;              // Half-hour increment index of event start.
                                        // (00:00 = 0, 23:30 = 47)
    private int endTime;                // Half-hour increment index of event end.
                                        // (00:00 = 0, 23:30 = 47)
    private String creator;
    private Set<String> participatingNodes;          // Set of node IP addresses
    private Set<String> unconfirmedNodes;            // Awaiting RSVP
    private int pending;             // Set to 0 if pending RSVP's
    
    public Appointment(String _name, int _day, int _s, int _e, String _creator, Set<String> _nodes) {
        this.name = _name;
        this.day = _day;
        this.startTime = _s;
        this.endTime = _e;
        this.creator = _creator;
        this.participatingNodes = new HashSet<> ();
        this.unconfirmedNodes = new HashSet<> ();
        if (_nodes != null) {
            this.participatingNodes.addAll(_nodes);         
            this.unconfirmedNodes.addAll(_nodes);
        }
    }
    
    public Appointment () {
        // Blank constructor
        this.participatingNodes = new HashSet<> ();
        this.unconfirmedNodes = new HashSet<> ();
    }
    
    /*
    Create a clone of this appointment.
    Participating nodes altered to reflect other nodes
    */
    public Appointment createCopyOfGroupApptForThisNode () {
        Appointment clone = new Appointment();
        clone.name = this.name;
        clone.day = this.day;
        clone.startTime = this.startTime;
        clone.endTime = this.endTime;
        clone.creator = DistroCal.getInstance().getThisNode().getAddress();
        
        Set<String> pn = new HashSet<>();
        pn.addAll(this.participatingNodes);
        pn.remove(DistroCal.getInstance().getThisNode().getAddress());
        pn.add(this.creator);
        
        clone.participatingNodes = new HashSet<>(pn);
        
        Set<String> un = new HashSet<>();
        un.addAll(this.unconfirmedNodes);
        un.remove(DistroCal.getInstance().getThisNode().getAddress());
        
        clone.unconfirmedNodes = new HashSet<>(un);
        
        return clone;
    }
    
    public void confirmNode (Node n) {
        unconfirmedNodes.remove(n.getAddress());
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String _name) {
        name = _name;
    }
    
    public int getDay() {
        return day;
    }
    
    public void setDay (int _d) {
        day = _d;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public void setStartTime (int _s) {
        startTime = _s;
    }
    
    public int getEndTime() {
        return endTime;
    }
    
    public void setEndTime(int _e) {
        endTime = _e;
    }
    
    public Set<String> getNodes() {
        return participatingNodes;
    }
    
    public void setNodes (Set<String> n) {
        participatingNodes = new HashSet<>(n);
        unconfirmedNodes = new HashSet<>(n);
    }
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator (String c) {
        creator = c;
    }
    
    public boolean getPending () {
        return (unconfirmedNodes.size() > 0);
    }
}
