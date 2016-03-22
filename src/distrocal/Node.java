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
    public Node(String _ip, int _port) {

        this.ip = _ip;
        this.port = _port;

    }

    /*
     Send a Message object to this Node
     */
    public void send(Message m) throws IOException {
        // Create socket connection for this Node
        try {
            this.socket = new Socket(ip, port);
            DataOutputStream ds = new DataOutputStream(socket.getOutputStream());
            this.outStream = new ObjectOutputStream(ds);

            outStream.writeObject(m);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     Predicate to determine if this Node has knowledge of an Event
     */
    public boolean hasRec(Event e, TimeMatrix t) {
        return t.get(this, e.getNode()) >= e.getTime();
    }
    
    public String getIP() {
        return this.ip;
    }

}
