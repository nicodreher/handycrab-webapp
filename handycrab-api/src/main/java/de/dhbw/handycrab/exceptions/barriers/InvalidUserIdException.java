package de.dhbw.handycrab.exceptions.barriers;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a user, different from the creator, tries to modify or delete a barrier.
 */
public class InvalidUserIdException extends HandyCrabException {
    public InvalidUserIdException() {
        super(400, 10);
    }
}
