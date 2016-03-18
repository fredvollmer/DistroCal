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

/**
 *
 * @author anniefischer
 */
public class RequestHandler implements HttpHandler {
    
      public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
           read(is); // .. read the request body
           String response = "This is the response";
           t.sendResponseHeaders(200, response.length());
           OutputStream os = t.getResponseBody();
           os.write(response.getBytes());
           os.close();
      }
}
