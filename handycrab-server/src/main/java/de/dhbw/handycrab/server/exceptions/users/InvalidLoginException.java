package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class InvalidLoginException extends HandyCrabException {
    public InvalidLoginException() {
        super(400, 6);
    }
}
