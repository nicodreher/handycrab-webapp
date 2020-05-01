package de.dhbw.handycrab.server.rest.errors;

import de.dhbw.handycrab.exceptions.HandyCrabException;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * The HandyCrabExceptionMapper is used to decide what happens if a {@link EJBException} is thrown in a RestService
 *
 * @author Nico Dreher
 * @see ExceptionMapper
 */
@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
    /**
     * Creates the response of the ExceptionMapper
     *
     * @param exception the thrown exception
     *
     * @return the response to the client
     *
     * @see ExceptionMapper#toResponse(Throwable)
     */
    @Override
    public Response toResponse(EJBException exception) {
        if(exception.getCausedByException() instanceof HandyCrabException) {
            return new HandyCrabExceptionMapper().toResponse((HandyCrabException) exception.getCause());
        }
        else {
            return new ThrowableExceptionMapper().toResponse(exception.getCause());
        }
    }
}