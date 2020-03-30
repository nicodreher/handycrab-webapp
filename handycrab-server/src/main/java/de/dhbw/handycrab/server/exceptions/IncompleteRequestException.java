package de.dhbw.handycrab.server.exceptions;

public class IncompleteRequestException extends HandyCrabException {
    public IncompleteRequestException() {
        super(400, 1);
    }
}
