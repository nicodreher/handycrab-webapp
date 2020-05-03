package de.dhbw.handycrab.api.users;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * A representation of a registered user without the password field for the REST-Clients
 * @author Nico Dreher
 */
public class FrontendUser implements Serializable {
    private ObjectId _id;
    private String username;
    private String email;

    public FrontendUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public FrontendUser() {
    }

    public FrontendUser(User user) {
        this._id = user.getID();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public ObjectId getID() {
        return _id;
    }
}
