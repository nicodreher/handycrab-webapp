package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class InvalidUsernameException extends HandyCrabException {

    public InvalidUsernameException() {
        super(400, 12);
    }

}
