package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a user registers with an E-Mail address, which doesn't match the {@link de.dhbw.handycrab.api.users.Users#EMAIL_REGEX} pattern
 * @author Nico Dreher
 */
public class InvalidMailException extends HandyCrabException {
    public InvalidMailException() {
        super(400, 5);
    }
}
