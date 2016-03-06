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

    public Node getNode() {
        return node;
    }
    
    public int getTime() {
        return time;
    }
}
