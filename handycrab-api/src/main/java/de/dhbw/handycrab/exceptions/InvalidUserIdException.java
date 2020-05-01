package de.dhbw.handycrab.exceptions;

public class InvalidUserIdException extends HandyCrabException {
    public InvalidUserIdException() {
        super(400, 10);
    }
}
