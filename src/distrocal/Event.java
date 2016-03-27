/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.Serializable;

/**
 *
 * @author s65q479
 */
public class Event implements Serializable {

    private EventType type;
    private Node node;
    private int time;
    private Appointment appointment;
    
    public Event (EventType t, Appointment c) {
        type = t;
        node = DistroCal.getInstance().getThisNode();
        time = DistroCal.getInstance().getTimeMatrix().getLogicalClock();
        appointment = c;
    }
    
     public Event (EventType t, Appointment c, Node n, int clock) {
        type = t;
        node = n;
        time = clock;
        appointment = c;
    }

    public Node getNode() {
        return node;
    }
    
    public int getTime() {
        return time;
    }
    
    public EventType getType () {
        return type;
    }
    
    public Appointment getAppointment () {
        return appointment;
    }
    
    public void setTime (int _t) {
        time = _t;
    }
}
