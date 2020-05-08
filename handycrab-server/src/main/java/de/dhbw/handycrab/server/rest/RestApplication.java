package de.dhbw.handycrab.server.rest;

import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.exceptions.InvalidObjectIdException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("")
public class RestApplication extends Application {
    /**
     * The default media type of all requests.
     */
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON;

    /**
     * Validates a {@link org.bson.types.ObjectId} from hexadecimal string
     * @param objectId
     * @return The valid ObjectId
     * @throws IncompleteRequestException If the ObjectId is invalid
     */
    public static String validateObjectId(String objectId) throws IncompleteRequestException {
        if(objectId != null) {
            if(objectId.matches("^[a-f0-9]{24}$")) {
                return objectId;
            }
            throw new InvalidObjectIdException();
        }
        throw new IncompleteRequestException();
    }
}
