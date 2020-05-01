package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class InvalidUsernameException extends HandyCrabException {

    public InvalidUsernameException() {
        super(400, 12);
    }

}
