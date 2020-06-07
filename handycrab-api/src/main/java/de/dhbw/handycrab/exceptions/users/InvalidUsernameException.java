package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a user registers an account with a username, which does not match the {@link de.dhbw.handycrab.api.users.Users#USERNAME_REGEX} pattern
 *
 * @author Nico Dreher
 */
public class InvalidUsernameException extends HandyCrabException {

    public InvalidUsernameException() {
        super(400, 12);
    }

}
