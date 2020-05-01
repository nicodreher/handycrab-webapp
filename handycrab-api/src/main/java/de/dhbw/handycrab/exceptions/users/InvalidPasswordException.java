package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class InvalidPasswordException extends HandyCrabException {
    public InvalidPasswordException() {
        super(400, 13);
    }
}
