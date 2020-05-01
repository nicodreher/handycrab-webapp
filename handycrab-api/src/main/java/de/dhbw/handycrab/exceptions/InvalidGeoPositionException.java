package de.dhbw.handycrab.exceptions;

public class InvalidGeoPositionException extends HandyCrabException {
    public InvalidGeoPositionException() {
        super(400, 8);
    }
}
