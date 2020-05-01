package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class UserNotFoundException extends HandyCrabException {
    public UserNotFoundException() {
        super(404, 7);
    }
}
