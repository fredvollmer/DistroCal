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

/**
 *
 * @author anniefischer
 */
public class RequestHandler implements HttpHandler {

    public void handle(HttpExchange t) throws IOException {
        // Create Jackson instance
        ObjectMapper jsonMapper = new ObjectMapper();

        InputStream is = t.getRequestBody();
        String response = "";
        String origin = "";
        int statusCode = 200;
        Headers requestHeaders = t.getRequestHeaders();
        Headers responseHeaders = t.getResponseHeaders();

        // Set origin to be allowed
        List<String> origins = requestHeaders.get("Origin");
        if (origins != null) {
            origin = origins.get(0);
            responseHeaders.set("Access-Control-Allow-Origin", origin);
        }

        switch (t.getRequestMethod()) {
            case "GET":
                read(is); // .. read the request body

                // Set content-type header to JSON
                responseHeaders.set("Content-Type", "application/JSON");

                // Return data package: events and instance status
                DataPackage p = new DataPackage();
                p.events = DistroCal.getInstance().getMyAppointmentsAsSet();
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
                // Create Appointment from received json
                Appointment a;

                try {
                    a = jsonMapper.readValue(is, Appointment.class);
                    if (a != null) {
                        a.setCreator(DistroCal.getInstance().getThisNode().getAddress());
                    }
                    if (DistroCal.getInstance().addAppointment(a)) {
                        statusCode = 201;

                        // Build new event
                        Event e = new Event(EventType.INSERT, a);
                        DistroCal.getInstance().getLog().createEvent(e);
                    } else {
                        statusCode = 505;
                    }
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Make sure nothing is left in input stream
                read(is);

                break;
            case "DELETE":
                try {
                    DeleteRequest r = jsonMapper.readValue(is, DeleteRequest.class);
                    Appointment d = DistroCal.getInstance().getAppointment(r.key);
                    // Build new event
                    Event e = new Event(EventType.DELETE, d);
                    DistroCal.getInstance().getLog().createEvent(e);
                    
                    // Remove appt from calendar
                    DistroCal.getInstance().deleteAppointment(r.key);

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
                break;

            case "LOCK":
                read(is); // .. read the request body

                DistroCal.crash();
                System.out.println("This node has CRASHED");
                break;

            case "UNLOCK":
                read(is); // .. read the request body

                DistroCal.recover();
                System.out.println("This node has RECOVERED");
                break;

            case "OPTIONS":
                read(is); // .. read the request body

                // Handle CORS preflight
                responseHeaders.add("Access-Control-Allow-Methods", "POST");
                responseHeaders.add("Access-Control-Allow-Methods", "GET");
                responseHeaders.add("Access-Control-Allow-Methods", "DELETE");
                responseHeaders.add("Access-Control-Allow-Methods", "LOCK");
                responseHeaders.add("Access-Control-Allow-Methods", "UNLOCK");
                responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
        }

        t.sendResponseHeaders(statusCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        System.out.println("HTTP Responder: Responded to "
                + t.getRequestMethod() + " request");
    }
}
