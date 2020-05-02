package de.dhbw.handycrab.api.users;

import java.io.Serializable;

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
