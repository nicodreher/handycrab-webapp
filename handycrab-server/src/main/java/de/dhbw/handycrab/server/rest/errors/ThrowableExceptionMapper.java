package de.dhbw.handycrab.server.rest.errors;

import com.mongodb.MongoException;
import de.dhbw.handycrab.exceptions.InvalidJSONException;
import de.dhbw.handycrab.exceptions.MongoDBException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * The ThrowableExceptionMapper is used to decide what happens if Exception is thrown in a RestService
 *
 * @author Nico Dreher
 * @see javax.ws.rs.ext.ExceptionMapper
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable>{
    /**
     * Creates the response of the ExceptionMapper
     *
     * @param exception the thrown exception
     *
     * @return the response to the client
     *
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(Throwable)
     */
    @Override
    public Response toResponse(Throwable exception) {
        if(exception instanceof WebApplicationException) {
            return Response.status(((WebApplicationException) exception).getResponse().getStatus()).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN + ";charset=UTF-8").build();
        }
        if(exception instanceof JSONException) {
            return new HandyCrabExceptionMapper().toResponse(new InvalidJSONException());
        }
        if(exception instanceof MongoException) {
            exception.printStackTrace();
            return new HandyCrabExceptionMapper().toResponse(new MongoDBException());
        }
        exception.printStackTrace();
        return Response.serverError().entity(new JSONObject().put("exception",
                new JSONObject().put("name", exception.getClass().getSimpleName())
                        .put("message", exception.getMessage())).toString()).type(MediaType.APPLICATION_JSON)
                .build();
    }

}