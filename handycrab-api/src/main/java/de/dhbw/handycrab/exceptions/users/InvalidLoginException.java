package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a REST-Client tries to login with invalid credentials
 * @author Nico Dreher
 */
public class InvalidLoginException extends HandyCrabException {
    public InvalidLoginException() {
        super(400, 6);
    }
}
