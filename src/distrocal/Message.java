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
public class Message implements Serializable {

    private Log partialLog;
    private TimeMatrix timeMatrix;

    public static void handleReceivedMessage(Message m) {
          // Do nothing if this node is "crashed"
          if (DistroCal.getInstance().isCrashed) return;
          
          // Increment clock
          DistroCal.getInstance().getTimeMatrix().incrementClock();
          
          // Update TimeMatrix based on received time table
          DistroCal.getInstance().getTimeMatrix().updateWithMatrix(m.timeMatrix);
          }
    }
