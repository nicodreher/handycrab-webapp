package de.dhbw.handycrab.api.pictures;

import de.dhbw.handycrab.exceptions.pictures.InvalidPictureFormatException;
import de.dhbw.handycrab.exceptions.pictures.PictureNotFoundException;
import de.dhbw.handycrab.exceptions.pictures.PictureToBigException;
import org.bson.types.ObjectId;

/**
 * A EJB-Interface to upload pictures to the mongodb and retrieve pictures from the mongodb
 *
 * @author Nico Dreher, Lukas Lautenschlager
 */
public interface Pictures {
    String LOOKUP = "java:app/server/PicturesBean!de.dhbw.handycrab.api.pictures.Pictures";

    /**
     * Retrieve a picture from the database
     * @param id The {@link ObjectId} of the picture
     * @return The Picture
     * @throws PictureNotFoundException If not Picture with the PictureId exists
     */
    Picture get(ObjectId id) throws PictureNotFoundException;

    /**
     * Uploads a picture to the database.
     * @param base64 The base64 encoded picture
     * @return The uploaded picture
     * @throws InvalidPictureFormatException If the picture ist not a valid png or jpeg image
     * @throws PictureToBigException If the base64 String is larger than 8 MB (8 * 1024^2)
     */
    Picture put(String base64) throws InvalidPictureFormatException, PictureToBigException;
}
