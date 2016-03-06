/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author fredvollmer
 */
public class CalendarEvent implements Serializable {
    private String name;                // Name of the event
    private int day;                    // Integer representation of day of week.
                                        // (Sunday = 0; Saturday = 6)
    private int startTime;              // Half-hour increment index of event start.
                                        // (00:00 = 0, 23:30 = 47)
    private int endTime;                // Half-hour increment index of event end.
                                        // (00:00 = 0, 23:30 = 47)
    private Set<String> nodes;          // Set of node IP addresses
    
    public CalendarEvent(String _name, int _day, int _s, int _e, String[] _nodes) {
        this.name = _name;
        this.day = _day;
        this.startTime = _s;
        this.endTime = _e;
        this.nodes.addAll(Arrays.asList(_nodes));
    }
    
    public String getName() {
        return name;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public int getEndTime() {
        return endTime;
    }
    
    public Set<String> getNodes() {
        return nodes;
    }
}
