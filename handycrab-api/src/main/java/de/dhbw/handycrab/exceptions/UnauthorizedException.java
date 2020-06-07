package de.dhbw.handycrab.exceptions;

/**
 * Thrown if a user tries to send a request without being authorized
 *
 * @author Nico Dreher
 */
public class UnauthorizedException extends HandyCrabException {
    public UnauthorizedException() {
        super(401, 2);
    }
}
