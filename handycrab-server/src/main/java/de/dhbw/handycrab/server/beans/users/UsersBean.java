package de.dhbw.handycrab.server.beans.users;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import de.dhbw.handycrab.api.users.LoggedInUser;
import de.dhbw.handycrab.api.users.Token;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.exceptions.UnauthorizedException;
import de.dhbw.handycrab.exceptions.users.*;
import de.dhbw.handycrab.server.beans.persistence.DataSource;
import de.dhbw.handycrab.server.beans.persistence.RequestBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link Users} interface
 *
 * @author Nico Dreher
 * @see Users
 */
@Stateless
@Remote(Users.class)
public class UsersBean implements Users {

    public static final long TOKEN_TTL = 60 * 60 * 24 * 30;
    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;
    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;
    private DataSource<User> dataSource;
    private DataSource<Token> tokensDataSource;
    private Random random;

    public UsersBean() {

    }

    public UsersBean(MongoClient client, Serializer serializer) {
        this.client = client;
        this.serializer = serializer;
        construct();
    }

    /**
     * Generates the sha512 hash value of a string
     *
     * @param value
     * @return The hashed string
     */
    public static String sha512(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(value.getBytes());
            BigInteger bigInteger = new BigInteger(1, bytes);

            return bigInteger.toString(16);
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostConstruct
    private void construct() {
        dataSource = new DataSource<>(User.class, "users", serializer, client);
        tokensDataSource = new DataSource<>(Token.class, "tokens", serializer, client);
        IndexOptions options = new IndexOptions().name("ttl").expireAfter(TOKEN_TTL, TimeUnit.SECONDS);
        client.getDatabase(System.getenv("mongo_database")).getCollection("tokens")
                .createIndex(new Document("created", 1), options);
        random = new Random();
    }

    @Override
    public User getUser(ObjectId id) {
        return dataSource.get(id);
    }

    @Override
    public User register(String email, String username, String password)
            throws NameAlreadyUsedException, AddressAlreadyUsedException, InvalidPasswordException,
                   InvalidUsernameException {
        if(email != null && username != null && password != null) {
            if(email.matches(EMAIL_REGEX)) {
                if(username.matches(USERNAME_REGEX)) {
                    if(password.matches(PASSWORD_REGEX)) {
                        if(!dataSource.contains(Filters.regex("email", "^" + email + "$", "i"))) {
                            if(!dataSource.contains(Filters.regex("username", "^" + username + "$", "i"))) {
                                User user = new User(email, username, sha512(password));
                                dataSource.insert(user);
                                return user;
                            }
                            else {
                                throw new NameAlreadyUsedException();
                            }
                        }
                        else {
                            throw new AddressAlreadyUsedException();
                        }
                    }
                    else {
                        throw new InvalidPasswordException();
                    }
                }
                else {
                    throw new InvalidUsernameException();
                }
            }
            else {
                throw new InvalidMailException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public LoggedInUser login(String login, String password, boolean createToken) {
        if(login != null && password != null) {
            User user = dataSource.findFirst(new RequestBuilder().filter(Filters.and(Filters
                            .or(Filters.regex("email", "^" + login + "$", "i"),
                                    Filters.regex("username", "^" + login + "$", "i")),
                    Filters.eq("password", sha512(password)))));
            if(user != null) {
                String token = null;
                if(createToken) {
                    byte[] randomBytes = new byte[2048];
                    random.nextBytes(randomBytes);
                    token = Base64.getEncoder().encodeToString(randomBytes);
                    Token t = new Token(user.getID(), token);
                    tokensDataSource.insert(t);
                }
                return new LoggedInUser(user, token);
            }
            else {
                throw new InvalidLoginException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public String getUsername(ObjectId id) {
        if(id != null) {
            if(dataSource.contains(id)) {
                return dataSource.get(id).getUsername();
            }
            else {
                throw new UserNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public boolean isAuthorized(ObjectId id) {
        return id != null && dataSource.contains(id);
    }

    @Override
    public boolean isAuthorized(ObjectId id, String token) {
        if(id != null && token != null && !token.isEmpty()) {
            return tokensDataSource.contains(Filters.and(Filters.eq("userId", id), Filters.eq("token", token)));
        }
        return false;
    }

    @Override
    public void checkAuthorized(ObjectId id) {
        if(!isAuthorized(id)) {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void removeToken(ObjectId id, String token) {
        if(id != null && token != null) {
            tokensDataSource.deleteOne(Filters.and(Filters.eq("userId", id), Filters.eq("token", token)));
        }
    }
}
