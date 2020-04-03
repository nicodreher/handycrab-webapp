package de.dhbw.handycrab.server.beans.users;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.server.beans.persistence.DataSource;
import de.dhbw.handycrab.server.beans.persistence.RequestBuilder;
import de.dhbw.handycrab.server.exceptions.*;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Stateless
@Remote(Users.class)
public class UsersBean implements Users {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final String USERNAME_REGEX = "[a-zA-Z0-9_]{4,16}";

    private static final String PASSWORD_REGEX = "[a-zA-Z0-9\"!#$%&'()*+,\\-./:;<=>?@\\[\\]]{6,100}";

    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    private DataSource<User> dataSource;

    public UsersBean() {

    }

    public UsersBean(MongoClient client, Serializer serializer) {
        this.client = client;
        this.serializer = serializer;
        construct();
    }

    @PostConstruct
    private void construct() {
        dataSource = new DataSource<>(User.class, "users", serializer, client);
    }

    @Override
    public User getUser(ObjectId id) {
        return dataSource.get(id);
    }

    @Override
    public User register(String email, String username, String password) {
        if(email != null && username != null && password != null) {
            email = email.toLowerCase();
            username = username.toLowerCase();
            if(email.matches(EMAIL_REGEX)) {
                if(username.matches(USERNAME_REGEX)) {
                    if(password.matches(PASSWORD_REGEX)) {
                        if(!dataSource.contains(Filters.eq("email", email))) {
                            if(!dataSource.contains(Filters.eq("username", username))) {
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
    public User login(String login, String password) {
        if(login != null && password != null) {
            User user = dataSource.findFirst(new RequestBuilder().filter(Filters.and(Filters.or(Filters.eq("email", login.toLowerCase()), Filters.eq("username", login.toLowerCase())), Filters.eq("password", sha512(password)))));
            if(user != null) {
                return user;
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
    public void checkAuthorized(ObjectId id) {
        if(!isAuthorized(id)) {
            throw new UnauthorizedException();
        }
    }

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
}
