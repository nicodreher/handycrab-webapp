package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class InvalidLoginException extends HandyCrabException {
    public InvalidLoginException() {
        super(400, 6);
    }
}
