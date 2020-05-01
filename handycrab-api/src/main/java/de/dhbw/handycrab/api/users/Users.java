package de.dhbw.handycrab.api.users;

import org.bson.types.ObjectId;

import javax.ws.rs.NotAuthorizedException;

/**
 * The EJB-Interface for everything related the the users
 * @author Nico Dreher
 */
public interface Users {
    String LOOKUP = "java:app/server/UsersBean!de.dhbw.handycrab.api.users.Users";

    /**
     * Get the user by the UserId.
     * @param id
     * @return The user if it exists. Otherwise returns null
     */
    User getUser(ObjectId id);

    /**
     * Register a new User.
     * @param email
     * @param username
     * @param password
     * @return The registered user
     */
    User register(String email, String username, String password);

    /**
     * Log the user in with the credentials.
     * @param login
     * @param password
     * @return The user
     */
    User login(String login, String password);

    /**
     * Get the username by the UserId.
     * @param id
     * @return The username
     */
    String getUsername(ObjectId id);

    /**
     * Check if a user with the UserId exists.
     * @param id
     * @return True if the user exists
     */
    boolean isAuthorized(ObjectId id);

    /**
     * Check if a user with the UserId exists.
     * @param id
     */
    void checkAuthorized(ObjectId id);
}
