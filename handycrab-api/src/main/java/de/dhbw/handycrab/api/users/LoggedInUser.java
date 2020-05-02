package de.dhbw.handycrab.api.users;

import java.io.Serializable;

/**
 * The LoggedInUser class is used to send the user data and the authentication token to the REST-Clients
 */
public class LoggedInUser implements Serializable {
    private User user;
    private String token;

    public LoggedInUser(User user) {
        this.user = user;
    }

    public LoggedInUser(User user, String token) {
        this(user);
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
