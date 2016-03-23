/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import static javax.imageio.ImageIO.read;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 * @author anniefischer
 */
public class RequestHandler implements HttpHandler {
    // Create Jackson instance
     ObjectMapper jsonMapper = new ObjectMapper();
     
      public void handle(HttpExchange t) throws IOException {
          InputStream is = t.getRequestBody();
          read(is); // .. read the request body
          String response = "";
          Headers requestHeaders = t.getRequestHeaders();
          String origin = requestHeaders.get("Origin").get(0);
          
          switch (t.getRequestMethod()) {
              case "GET":
                  // Return data package: events and instance status
                  DataPackage p = new DataPackage();
                  p.events = DistroCal.getInstance().
                  break;
              case "POST":
                  // Create new event
                  break;
              case "DELETE":
                  // Crash this node
                  break;
              case "OPTIONS":
                  // Handle CORS preflight
                  Headers responseHeaders = t.getResponseHeaders();
                  List<String> allowedMethods = new LinkedList<>();
                  allowedMethods.add("POST");
                  
                  // Send headers
                  responseHeaders.set("Access-Control-Allow-Origin", origin);
                  responseHeaders.add("Access-Control-Allow-Methods", "POST");
                  responseHeaders.add("Access-Control-Allow-Methods", "GET");
                  responseHeaders.add("Access-Control-Allow-Methods", "DELETE");
                  
          }
           
           t.sendResponseHeaders(200, response.length());
           OutputStream os = t.getResponseBody();
           os.write(response.getBytes());
           os.close();
           
           System.out.println("HTTP Responder: Responded to request from " + origin);
      }
}
