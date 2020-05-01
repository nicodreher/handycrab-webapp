package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class NameAlreadyUsedException extends HandyCrabException {
    public NameAlreadyUsedException() {
        super(400, 4);
    }
}
