/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author s65q479
 */
public class Node implements Serializable {

    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream outStream;

    /*
     Constructor
     */
    public Node(String _address) {

        String addressParts[] = _address.split(":");
        this.ip = addressParts[0];
        this.port = Integer.parseInt(addressParts[1]);

    }
    
    /*
    Create a partial log to send to this node
    */
    public Log createPartialLog () {
        // Iterate over every event in this instances log
        Log thisLog = DistroCal.getInstance().getLog();
        Log pl = new Log();
        for (Event e : thisLog.getEvents()) {
            // check if this event needs to be included
            if (!hasRec(e)) {
                pl.add(e);
            }
        }
        return pl;
    }

    /*
     Send a Message object to this Node
     */
    public void send(Message m) throws IOException {
        // Increment clock
        DistroCal.getInstance().getTimeMatrix().incrementClock();
        
        // Create socket connection for this Node
        try {
            this.socket = new Socket(ip, port);
            DataOutputStream ds = new DataOutputStream(socket.getOutputStream());
            this.outStream = new ObjectOutputStream(ds);

            outStream.writeObject(m);
            
            this.socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
       /*
     Check if this node is available over a time range
     Start time is inclusive, end is exclusive
     */
    public boolean isAvailable(int day, int start, int end) {
        // Iterate over each time slot, checking if key exists
        for (; start < end; start++) {
            if (DistroCal.getInstance().getAppointments().containsKey(day + "-" + start + "-" + getAddress())) {
                return false;
            }
        }
        return true;
    }

    /*
     Predicate to determine if this Node has knowledge of an Event
     */
    public boolean hasRec(Event e) {
        TimeMatrix t = DistroCal.getInstance().getTimeMatrix();
        return t.get(this, e.getNode()) >= e.getTime();
    }
    
    /*
    Returns a stirng in the format [ip]:[port] used as a key in the time matrix
    */
    public String getAddress() {
        return this.ip + ":" + this.port;
    }
    
    /*
    Return the listener port for this node
    */
    public int getPort() {
        return this.port;
    }
}
