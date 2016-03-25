/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distrocal;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.InputStream;
import static javax.imageio.ImageIO.read;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.util.List;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.read;

/**
 *
 * @author anniefischer
 */
public class RequestHandler implements HttpHandler {

    public void handle(HttpExchange t) throws IOException {
        InputStream is = t.getRequestBody();
        read(is); // .. read the request body
        String response = "";
        Headers requestHeaders = t.getRequestHeaders();
        Headers responseHeaders = t.getResponseHeaders();

        switch (t.getRequestMethod()) {
            case "GET":
                // Create Jackson instance
                ObjectMapper jsonMapper = new ObjectMapper();
                
                // Set content-type header to JSON
                responseHeaders.set("Content-Type", "application/JSON");

                // Return data package: events and instance status
                DataPackage p = new DataPackage();
                p.events = DistroCal.getInstance().getAppointmentsAsSet();
                p.status = (DistroCal.getInstance().isCrashed) ? 0 : 1;
                try {
                    response = jsonMapper.writeValueAsString(p);
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "POST":
                // Create new event
                break;
            case "DELETE":
                break;
                
            case "LOCK":
                DistroCal.crash();
                System.out.println("This node has CRASHED");
                break;
                
            case "UNLOCK":
                DistroCal.recover();
                System.out.println("This node has RECOVERED");
                break;
                     
            case "OPTIONS":
                // Handle CORS preflight
                String origin = requestHeaders.get("Origin").get(0);

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

        System.out.println("HTTP Responder: Responded to "
                + t.getRequestMethod() + " request");
    }
}
