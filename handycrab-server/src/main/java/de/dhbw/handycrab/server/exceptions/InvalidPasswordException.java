package de.dhbw.handycrab.server.exceptions;

public class InvalidPasswordException extends HandyCrabException {
    public InvalidPasswordException() {
        super(400, 13);
    }
}
