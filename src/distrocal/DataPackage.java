/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.util.List;
import java.util.Set;


/**
 *
 * @author s65q479
 */
public class DataPackage {
    public Set<Appointment> events;
    public int status;
    
    public Set<Appointment> getEvents () {
        return events;
    }
    
    public int getStatus() {
        return status;
    }
}
