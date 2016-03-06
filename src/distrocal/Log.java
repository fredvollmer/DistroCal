/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distrocal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author s65q479
 */
public class Log implements Serializable {
    private Set<Event> events;
    
    public Log () {
        events = new HashSet<>();
    }
    
    public void add (Event e) {
        events.add(e);
    }
}
