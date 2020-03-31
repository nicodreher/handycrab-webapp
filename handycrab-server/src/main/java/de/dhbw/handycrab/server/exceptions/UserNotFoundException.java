package de.dhbw.handycrab.server.exceptions;

public class UserNotFoundException extends HandyCrabException {
    public UserNotFoundException() {
        super(404, 7);
    }
}
