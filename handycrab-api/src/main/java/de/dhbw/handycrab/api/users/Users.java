package de.dhbw.handycrab.api.users;

import org.bson.types.ObjectId;


public interface Users {
    String LOOKUP = "java:app/server/UsersBean!de.dhbw.handycrab.api.users.Users";
    User getUser(ObjectId id);
    User register(String email, String username, String password);
    User login(String login, String password);
    String getUsername(ObjectId id);
    boolean isAuthorized(ObjectId id);
    void checkAuthorized(ObjectId id);
}
