package de.dhbw.handycrab.server.rest;

import com.mongodb.MongoSocketWriteException;
import de.dhbw.handycrab.exceptions.users.InvalidUsernameException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

@Path("/error")
public class Error {
    @GET
    @Path("/java")
    public String createJavaException() throws IOException {
        throw new IOException("DEMO");
    }

    @GET
    @Path("/mongo")
    public String createMongoException() {
        throw new MongoSocketWriteException("DEMO Exception", null, new IOException("DEMO"));
    }

    @GET
    @Path("/handycrab")
    public String createHandyCrabException() {
        throw new InvalidUsernameException();
    }
}
