package de.dhbw.handycrab.api.pictures;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * The representation of the pictures
 *
 * @author Nico Dreher
 */
public class Picture implements Serializable {
    private ObjectId _id;
    /**
     * The base64 encoded picture
     */
    private String base64;
    /**
     * The http media type of the picture
     */
    private String contentType;

    public Picture(String base64, String contentType) {
        this.base64 = base64;
        this.contentType = contentType;
    }

    public String getBase64() {
        return base64;
    }

    public String getContentType() {
        return contentType;
    }

    public ObjectId getID() {
        return _id;
    }
}
