package de.dhbw.handycrab.server.rest.errors;

import de.dhbw.handycrab.exceptions.HandyCrabException;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * The HandyCrabExceptionMapper is used to decide what happens if a HandyCrabException is thrown in a RestService
 *
 * @author Nico Dreher
 * @see javax.ws.rs.ext.ExceptionMapper
 */
@Provider
public class HandyCrabExceptionMapper implements ExceptionMapper<HandyCrabException> {
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
    public Response toResponse(HandyCrabException exception) {
        return Response.status(exception.getStatusCode()).entity(new JSONObject().put("errorCode", exception.getErrorCode()).toString()).type(MediaType.APPLICATION_JSON).build();
    }
}