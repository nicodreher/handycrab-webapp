package de.dhbw.handycrab.exceptions;

public class IncompleteRequestException extends HandyCrabException {
    public IncompleteRequestException() {
        super(400, 1);
    }
}
