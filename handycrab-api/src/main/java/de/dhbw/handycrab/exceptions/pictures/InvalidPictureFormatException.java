package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class InvalidPictureFormatException extends HandyCrabException {
    public InvalidPictureFormatException() {
        super(400, 15);
    }
}
