package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a user tries to register a account with a password, which doesn't match the {@link de.dhbw.handycrab.api.users.Users#PASSWORD_REGEX} pattern
 * @author Nico Dreher
 */
public class InvalidPasswordException extends HandyCrabException {
    public InvalidPasswordException() {
        super(400, 13);
    }
}
