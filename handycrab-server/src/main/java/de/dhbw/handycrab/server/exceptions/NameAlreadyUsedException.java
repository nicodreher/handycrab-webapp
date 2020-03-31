package de.dhbw.handycrab.server.exceptions;

public class NameAlreadyUsedException extends HandyCrabException {
    public NameAlreadyUsedException() {
        super(400, 5);
    }
}
