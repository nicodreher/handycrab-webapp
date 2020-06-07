package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * An exception to throw if a picture was not found in the database
 *
 * @author Nico Dreher
 */
public class PictureNotFoundException extends HandyCrabException {
    public PictureNotFoundException() {
        super(404, 16);
    }
}
