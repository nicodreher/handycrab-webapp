package de.dhbw.handycrab.server.exceptions;

public class UnauthorizedException extends HandyCrabException {
    public UnauthorizedException() {
        super(401, 2);
    }
}
