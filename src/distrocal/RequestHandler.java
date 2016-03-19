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
import java.util.List;

/**
 *
 * @author anniefischer
 */
public class RequestHandler implements HttpHandler {
    
      public void handle(HttpExchange t) throws IOException {
          switch (t.getRequestMethod()) {
              case "GET":
                  // Return data package
                  break;
              case "POST":
                  // Create new event
                  break;
              case "DELETE":
                  // Crash this node
                  break;
              case "OPTIONS":
                  // Handle CORS preflight
                  // Get origin
                  Headers requestHeaders = t.getRequestHeaders();
                  String origin = requestHeaders.get("Origin").get(0);
                  Headers responseHeaders = t.getResponseHeaders();
                  List<String> allowedMethods = new LinkedList<>();
                  
                  // Send headers
                  responseHeaders.set("Access-Control-Allow-Origin", origin);
                  responseHeaders.set("Access-Control-Allow-Methods", origin);
                  
          }
            InputStream is = t.getRequestBody();
           read(is); // .. read the request body
           String response = "This is the response";
           t.sendResponseHeaders(200, response.length());
           OutputStream os = t.getResponseBody();
           os.write(response.getBytes());
           os.close();
      }
}
