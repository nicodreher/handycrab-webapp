package de.dhbw.handycrab.server.exceptions;

public class InvalidGeoPositionException extends HandyCrabException {
    public InvalidGeoPositionException() {
        super(400, 8);
    }
}
