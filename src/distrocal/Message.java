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

    private MessageType type;
    private Log partialLog;
    private TimeMatrix timeMatrix;

    public static void handleReceivedMessage(Message m) {
          // Do nothing if this node is "crashed"
          if (DistroCal.getInstance().isCrashed) return;
          
          switch (m.type) {
              case CRASH:
                  // "Crash" this node
                  DistroCal.getInstance().isCrashed = true;
                  break;
              case REVIVE:
                  DistroCal.getInstance().isCrashed = false;
                  break;
              case UPDATE:
                  // Update time matrix
                  
                  // Update log
                  
                  break;
                  
              case RETRIEVE:
                  // Send JSON package to client
                  
                  break;
          }
    }
}
