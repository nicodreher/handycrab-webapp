package de.dhbw.handycrab.exceptions;

/**
 * Thrown if a REST-Client sends a request with missing parameters
 *
 * @author Nico Dreher
 */
public class IncompleteRequestException extends HandyCrabException {
    public IncompleteRequestException() {
        super(400, 1);
    }
}
