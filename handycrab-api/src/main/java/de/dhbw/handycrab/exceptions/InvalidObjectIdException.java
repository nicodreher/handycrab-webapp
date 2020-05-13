package de.dhbw.handycrab.exceptions;

public class InvalidObjectIdException extends HandyCrabException {
    public InvalidObjectIdException() {
        super(400, 18);
    }
}
