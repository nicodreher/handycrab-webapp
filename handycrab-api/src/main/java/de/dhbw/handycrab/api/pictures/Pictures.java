package de.dhbw.handycrab.api.pictures;

import org.bson.types.ObjectId;

/**
 * A EJB-Interface to upload pictures to the mongodb and retrieve pictures from the mongodb.
 *
 * @author Nico Dreher, Lukas Lautenschlager
 */
public interface Pictures {
    String LOOKUP = "java:app/server/PicturesBean!de.dhbw.handycrab.api.pictures.Pictures";

    /**
     * Retrieve a picture from the database.
     * @param id The {@link ObjectId} of the picture
     * @return The Picture
     */
    Picture get(ObjectId id);

    /**
     * Uploads a picture to the database.
     * @param base64 The base64 encoded picture
     * @return The uploaded picture
     */
    Picture put(String base64);
}
