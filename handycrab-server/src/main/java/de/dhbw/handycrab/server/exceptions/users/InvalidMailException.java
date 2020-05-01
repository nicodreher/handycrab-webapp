package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class InvalidMailException extends HandyCrabException {
    public InvalidMailException() {
        super(400, 5);
    }
}
