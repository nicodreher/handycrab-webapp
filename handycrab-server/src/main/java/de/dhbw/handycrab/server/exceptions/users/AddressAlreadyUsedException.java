package de.dhbw.handycrab.server.exceptions.users;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class AddressAlreadyUsedException extends HandyCrabException {
    public AddressAlreadyUsedException() {
        super(400, 3);
    }
}
