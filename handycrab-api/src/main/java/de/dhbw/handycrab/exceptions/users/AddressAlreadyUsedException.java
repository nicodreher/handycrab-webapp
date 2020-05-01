package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class AddressAlreadyUsedException extends HandyCrabException {
    public AddressAlreadyUsedException() {
        super(400, 3);
    }
}
