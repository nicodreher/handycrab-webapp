package de.dhbw.handycrab.api.users;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

/**
 * A token gets created for a user to bypass the authentication for 30 days after a successful authentication
 *
 * @author Nico Dreher
 */
public class Token implements Serializable {
    private ObjectId _id;
    private Date created = new Date();
    private ObjectId userId;
    private String token;

    public Token(ObjectId userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
