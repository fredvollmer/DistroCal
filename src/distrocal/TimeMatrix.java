/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distrocal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author s65q479
 */
public class TimeMatrix implements Serializable {
    private Map<Node, Map<Node, Integer>> table;        // 2D map
    
    public TimeMatrix (Set<Node> nodes) {
        table = new HashMap<>();
        for (Node i : nodes) {
            table.put(i, new HashMap<>());
            for (Node j : nodes) {
                table.get(i).put(j, 0);
            }
        }
    }
    
    public void updateWithMatrix(TimeMatrix m) {
        
    }
}
