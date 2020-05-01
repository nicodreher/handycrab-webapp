package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class InvalidMailException extends HandyCrabException {
    public InvalidMailException() {
        super(400, 5);
    }
}
