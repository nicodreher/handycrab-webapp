package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class NameAlreadyUsedException extends HandyCrabException {
    public NameAlreadyUsedException() {
        super(400, 4);
    }
}
