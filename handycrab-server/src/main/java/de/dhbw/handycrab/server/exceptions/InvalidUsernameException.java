package de.dhbw.handycrab.server.exceptions;

public class InvalidUsernameException extends HandyCrabException {

    public InvalidUsernameException() {
        super(400, 12);
    }

}
