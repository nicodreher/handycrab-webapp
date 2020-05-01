package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class PictureToBigException extends HandyCrabException {
    public PictureToBigException() {
        super(400, 14);
    }
}
