package de.dhbw.handycrab.server.exceptions;

public class InvalidLoginException extends HandyCrabException {
    public InvalidLoginException() {
        super(400, 6);
    }
}
