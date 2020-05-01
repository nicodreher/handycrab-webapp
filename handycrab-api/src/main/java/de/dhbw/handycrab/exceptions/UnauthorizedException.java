package de.dhbw.handycrab.exceptions;

public class UnauthorizedException extends HandyCrabException {
    public UnauthorizedException() {
        super(401, 2);
    }
}
