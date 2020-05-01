package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class InvalidPasswordException extends HandyCrabException {
    public InvalidPasswordException() {
        super(400, 13);
    }
}
