package de.dhbw.handycrab.server.exceptions;

public class AddressAlreadyUsedException extends HandyCrabException {
    public AddressAlreadyUsedException() {
        super(400, 3);
    }
}
