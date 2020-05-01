package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class UserNotFoundException extends HandyCrabException {
    public UserNotFoundException() {
        super(404, 7);
    }
}
