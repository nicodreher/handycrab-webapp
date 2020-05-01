package de.dhbw.handycrab.server.exceptions.pictures;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class InvalidPictureFormatException extends HandyCrabException {
    public InvalidPictureFormatException() {
        super(400, 15);
    }
}
