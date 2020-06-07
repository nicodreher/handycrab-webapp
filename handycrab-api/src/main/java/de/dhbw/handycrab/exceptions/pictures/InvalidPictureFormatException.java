package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * An exception to indicate a wrong image format
 *
 * @author Nico Dreher
 */
public class InvalidPictureFormatException extends HandyCrabException {
    public InvalidPictureFormatException() {
        super(400, 15);
    }
}
