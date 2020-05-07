package de.dhbw.handycrab.exceptions.barriers;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown when a location (longitude, latitude) is not valid
 */
public class InvalidGeoPositionException extends HandyCrabException {
    public InvalidGeoPositionException() {
        super(400, 8);
    }
}
