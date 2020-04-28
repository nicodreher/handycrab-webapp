package de.dhbw.handycrab.server.exceptions;

public class InvalidUserIdException extends HandyCrabException {
    public InvalidUserIdException() {
        super(400, 10);
    }
}
