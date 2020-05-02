package de.dhbw.handycrab.api.users;

import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.exceptions.UnauthorizedException;
import de.dhbw.handycrab.exceptions.users.*;
import org.bson.types.ObjectId;

/**
 * The EJB-Interface for everything related the the users
 * @author Nico Dreher
 */
public interface Users {
    String LOOKUP = "java:app/server/UsersBean!de.dhbw.handycrab.api.users.Users";

    /**
     * The regex pattern for the E-Mail addresses
     */
    String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    /**
     * The regex pattern for the username
     */
    String USERNAME_REGEX = "[a-zA-Z0-9_]{4,16}";
    /**
     * The regex pattern for the password
     */
    String PASSWORD_REGEX = "[a-zA-Z0-9\"!#$%&'()*+,\\-./:;<=>?@\\[\\]]{6,100}";

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
     * @throws IncompleteRequestException If a argument ist null or empty
     * @throws InvalidMailException If the E-Mail address does not match the {@link Users#EMAIL_REGEX} pattern
     * @throws InvalidUsernameException If the username does not match the {@link Users#USERNAME_REGEX} pattern
     * @throws InvalidPasswordException If the password does not match the {@link Users#PASSWORD_REGEX} pattern
     * @throws AddressAlreadyUsedException If the E-Mail address ist already used by a user
     * @throws NameAlreadyUsedException If the Username is already used by a user
     */
    User register(String email, String username, String password) throws IncompleteRequestException,
            InvalidMailException, InvalidUsernameException, InvalidPasswordException, AddressAlreadyUsedException,
            NameAlreadyUsedException;

    /**
     * Log the user in with the credentials.
     * @param login The E-Mail address or the username
     * @param password
     * @return The logged in user
     * @throws IncompleteRequestException If a argument is null or empty
     * @throws InvalidLoginException If the login credentials are wrong
     */
    LoggedInUser login(String login, String password, boolean createToken) throws IncompleteRequestException, InvalidLoginException;

    /**
     * Get the username by the UserId.
     * @param id
     * @return The username
     * @throws IncompleteRequestException If a argument is null
     * @throws UserNotFoundException If no user with the UserId exists
     */
    String getUsername(ObjectId id) throws IncompleteRequestException, UserNotFoundException;

    /**
     * Check if a user with the UserId exists.
     * @param id
     * @return True if the user exists
     */
    boolean isAuthorized(ObjectId id);

    boolean isAuthorized(ObjectId id, String token);

    void removeToken(ObjectId id, String token);

    /**
     * Check if a user with the UserId exists.
     * @param id
     * @throws UnauthorizedException if the user does not exist
     */
    void checkAuthorized(ObjectId id) throws UnauthorizedException;
}
