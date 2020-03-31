package de.dhbw.handycrab.server.exceptions;

public class InvalidMailException extends HandyCrabException {
    public InvalidMailException() {
        super(400, 5);
    }
}
