package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

public class PictureNotFoundException extends HandyCrabException {
    public PictureNotFoundException() {
        super(404, 16);
    }
}
