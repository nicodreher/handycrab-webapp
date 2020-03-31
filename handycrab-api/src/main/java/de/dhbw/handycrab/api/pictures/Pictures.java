package de.dhbw.handycrab.api.pictures;

import org.bson.types.ObjectId;

public interface Pictures {
    String LOOKUP = "java:app/server/PicturesBean!de.dhbw.handycrab.api.pictures.Pictures";
    Picture get(ObjectId id);
    ObjectId put(Picture picture);
}
