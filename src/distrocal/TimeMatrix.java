/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distrocal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author s65q479
 */
public class TimeMatrix implements Serializable {
    private Map<String, Map<String, Integer>> table;        // 2D map
    
    public TimeMatrix (Set<Node> nodes) {
        table = new HashMap<>();
        for (Node i : nodes) {
            table.put(i.getAddress(), new HashMap<>());
            for (Node j : nodes) {
                table.get(i.getAddress()).put(j.getAddress(), 0);
            }
        }
    }
    
    public int get(Node row, Node col) {
        return table.get(row.getAddress()).get(col.getAddress());
    }
    
    public void set(Node row, Node col, int t) {
        table.get(row.getAddress()).put(col.getAddress(), t);
    }
    
    /*
    Increment this node's logical clock
    */
    public void incrementClock () {
        Node me = DistroCal.getInstance().getThisNode();
        int t = get(me, me);
        set(me, me, ++t);
    }
    
    public void updateWithMatrix(TimeMatrix m) {
        // Build set of ALL nodes (includes this node)
        Set<Node> allNodes = new HashSet<> ();
        allNodes.addAll(DistroCal.getInstance().getOtherNodes());
        allNodes.add(DistroCal.getInstance().getThisNode());
        Set<Node> otherNodes = DistroCal.getInstance().getOtherNodes();
        
        // First, we update this node's matrix with values from the received matrix
        // Directly copy all cells except this node's row
        for (Node i : otherNodes) {     // For all rows except this node's
            for (Node j : allNodes) {   // For every column
                // Get this cell from received matrix
                int t1 = m.get(i, j);
                // Replace corresponding cell in this node's matrix
                set(i, j, t1);
            }
        }
        
        // Now for this node's row...find max of each other node's column
        // Iterate over each column (for other nodes)
        // Don't need to update what we know about ourselves
        for (Node col : otherNodes) {
            // Find max
            int max = -1;
            for (Node row : allNodes) {
                max = (get(row, col) > max) ? get(row, col) : max;
            }
            
            // Set what we know about this other node to be max
            set(DistroCal.getInstance().getThisNode(), col, max);
        }
    }
}
