package de.dhbw.handycrab.api.users;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * The representation of a registered user
 *
 * @author Nico Dreher
 */
public class User implements Serializable {
    private ObjectId _id;
    private String username;
    private String email;
    /**
     * The sha512 hashed password
     */
    private String password;

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ObjectId getID() {
        return _id;
    }
}
