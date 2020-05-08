package de.dhbw.handycrab.exceptions;

public class InvalidJSONException extends HandyCrabException {
    public InvalidJSONException() {
        super(400, 17);
    }
}
