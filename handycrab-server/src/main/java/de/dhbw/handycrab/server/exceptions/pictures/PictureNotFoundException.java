package de.dhbw.handycrab.server.exceptions.pictures;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;

public class PictureNotFoundException extends HandyCrabException {
    public PictureNotFoundException() {
        super(404, 16);
    }
}
