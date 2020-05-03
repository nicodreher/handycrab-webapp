package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a client requests a non existing username
 */
public class UserNotFoundException extends HandyCrabException {
    public UserNotFoundException() {
        super(404, 7);
    }
}
