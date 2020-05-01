package de.dhbw.handycrab.server.exceptions.pictures;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class PictureToBigException extends HandyCrabException {
    public PictureToBigException() {
        super(400, 14);
    }
}
