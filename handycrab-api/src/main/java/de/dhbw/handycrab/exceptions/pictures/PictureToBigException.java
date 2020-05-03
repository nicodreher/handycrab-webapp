package de.dhbw.handycrab.exceptions.pictures;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown when a REST-Client uploads a image larger than 8 MB (8 * 1024^2 Bytes)
 * @author Nico Dreher
 */
public class PictureToBigException extends HandyCrabException {
    public PictureToBigException() {
        super(400, 14);
    }
}
