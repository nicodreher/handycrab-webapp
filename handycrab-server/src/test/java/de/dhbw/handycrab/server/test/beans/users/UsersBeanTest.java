package de.dhbw.handycrab.server.test.beans.users;

import static org.junit.jupiter.api.Assertions.*;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.dhbw.handycrab.api.users.LoggedInUser;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.server.beans.users.UsersBean;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.exceptions.users.InvalidLoginException;
import de.dhbw.handycrab.exceptions.UnauthorizedException;
import de.dhbw.handycrab.exceptions.users.UserNotFoundException;
import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Tests for the {@link UsersBean} class.
 *
 * @author Nico Dreher
 */
@Testcontainers
class UsersBeanTest {

    @Container
    private MongoContainer container = new MongoContainer();

    /**
     * Generates a bson {@link Document} with userdata in it.
     * @param id
     * @param email
     * @param username
     * @param password the plain password of the account
     * @return the user document
     */
    private Document generateUser(ObjectId id, String email, String username, String password) {
        Document doc = new Document();
        doc.put("_id", id);
        doc.put("email", email);
        doc.put("username", username);
        doc.put("password", UsersBean.sha512(password));
        return doc;
    }

    /**
     * Generates four user documents.
     * @return four user documents
     */
    private Document[] generateUsers() {
        return new Document[]{generateUser(new ObjectId("000000000000000000000000"), "bla@blabla.de", "blabla", "pass1234"),
                generateUser(new ObjectId("000000000000000000000001"), "nanana@nanana.de", "nanana", "nanapass"),
                generateUser(new ObjectId("000000000000000000000002"), "example@example.de", "example", "examplepass"),
                generateUser(new ObjectId("000000000000000000000003"), "king@kong.de", "king", "kingpass")};
    }

    /**
     * Inserts users into the mongodb
     * @return a map of the username and the id of the inserted users
     */
    private Map<String, ObjectId> insertUsers() {
        Document[] docs = generateUsers();
        Map<String, ObjectId> users = new HashMap<>();
        for(Document doc : docs) {
            container.getCollection("users").insertOne(doc);
            users.put(doc.getString("username"), doc.getObjectId("_id"));
        }
        return users;
    }

    /**
     * Tests the {@link UsersBean#register(String, String, String)} function with valid inputs.
     * @param email
     * @param username
     * @param password the plain password
     */
    @ParameterizedTest(name = "[{index}] Email: {0} Username: {1} Password: {2}")
    @CsvFileSource(resources = "/users/register.csv")
    void registerTest(String email, String username, String password) {
        insertUsers();
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        User user = bean.register(email, username, password);
        assertNotNull(user.getID());
        Bson filter = and(eq("email", user.getEmail()), eq("username", user.getUsername()));
        assertEquals(1, container.getCollection("users").countDocuments(filter));
        FindIterable<Document> result = container.getCollection("users").find(filter);
        Document first = result.first();
        assertNotNull(first);
        assertEquals(user.getID(), first.getObjectId("_id"));
    }

    /**
     * Tests the {@link UsersBean#register(String, String, String)} function with invalid data.
     * @param comment Information about the invalid data
     * @param email
     * @param username
     * @param password
     * @param expectedException The exception which should be thrown
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvFileSource(resources = "/users/failregister.csv")
    void failRegisterTest(String comment, String email, String username, String password, String expectedException) {
        Map<String, ObjectId> users = insertUsers();
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        final User[] user = new User[1];
        try {
            assertThrows((Class<? extends Throwable>) Class.forName(expectedException) , () -> user[0] = bean.register(email, username, password));
        }
        catch (ClassNotFoundException | ClassCastException e) {
            fail(e);
        }
        assertNull(user[0]);
        Bson filter = null;
        if(email != null && username != null) {
            filter = or(eq("email", email), eq("username", username));
        }
        else if(email != null) {
            filter = eq("email", email);
        }
        else if(username != null) {
            filter = eq("username", username);
        }
        if(filter != null && container.getCollection("users").countDocuments(filter) != 0) {
            container.getCollection("users").find(filter).forEach((Consumer<Document>) doc -> {
                assertTrue(users.containsKey(doc.getString("username")));
                assertEquals(users.get(doc.getString("username")), doc.getObjectId("_id"));
            });
        }
    }

    /**
     * Tests the {@link UsersBean#login(String, String, boolean)} function with valid and invalid data without creating a token.
     * @param comment Information about the data
     * @param email E-Mail address of the generated user
     * @param username Username of the generated user
     * @param password Plain password of the generated user
     * @param login The username or password of the login
     * @param loginPassword The login password
     * @param correct True if the login should be successful
     * @param complete True if the request ist complete
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvFileSource(resources = "/users/login.csv")
    void loginTest(String comment, String email, String username, String password, String login, String loginPassword, boolean correct, boolean complete) {
        Document doc = generateUser(new ObjectId(), email, username, password);
        container.getCollection("users").insertOne(doc);
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        if(correct) {
            LoggedInUser user = bean.login(login, loginPassword, false);
            assertNotNull(user);
            assertNotNull(user.getUser());
            assertNull(user.getToken());
            assertEquals(doc.getObjectId("_id"), user.getUser().getID());
        }
        else {
            Class<? extends  Throwable> exception = complete ? InvalidLoginException.class : IncompleteRequestException.class;
            final LoggedInUser[] user = new LoggedInUser[1];
            assertThrows(exception, () -> user[0] = bean.login(login, loginPassword, false));
            assertNull(user[0]);
        }
    }


    /**
     * Tests the {@link UsersBean#login(String, String, boolean)} function with the creation of keep signed in tokens.
     * @param comment Information about the data
     * @param email E-Mail address of the generated user
     * @param username Username of the generated user
     * @param password Plain password of the generated user
     * @param login The username or password of the login
     * @param loginPassword The login password
     * @param correct True if the login should be successful
     * @param complete True if the request ist complete
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvFileSource(resources = "/users/login.csv")
    void loginWithTokenTest(String comment, String email, String username, String password, String login, String loginPassword, boolean correct, boolean complete) throws InterruptedException {
        Document doc = generateUser(new ObjectId(), email, username, password);
        container.getCollection("users").insertOne(doc);

        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean(), 5);
        if(correct) {
            LoggedInUser user = bean.login(login, loginPassword, true);
            assertNotNull(user);
            assertNotNull(user.getUser());
            assertNotNull(user.getToken());
            assertEquals(doc.getObjectId("_id"), user.getUser().getID());
            Document tokenDoc = container.getCollection("tokens").find(Filters.and(Filters.eq("userId", user.getUser().getID()), Filters.eq("token", user.getToken()))).first();
            assertNotNull(tokenDoc);
            assertEquals(user.getUser().getID(), tokenDoc.get("userId"));
            assertEquals(user.getToken(), tokenDoc.getString("token"));
        }
        else {
            Class<? extends  Throwable> exception = complete ? InvalidLoginException.class : IncompleteRequestException.class;
            final LoggedInUser[] user = new LoggedInUser[1];
            assertThrows(exception, () -> user[0] = bean.login(login, loginPassword, true));
            assertNull(user[0]);
            assertEquals(0, container.getCollection("tokens").countDocuments());
        }
    }


    /**
     * Tests the {@link UsersBean#isAuthorized(ObjectId, String)} function with valid and invalid userIds and tokens.
     */
    @Test
    public void authWithTokenTest() {
        String token = "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";
        ObjectId userId = new ObjectId("000000000000000000000000");
        Document doc = new Document().append("token", token).append("userId", userId);
        container.getCollection("tokens").insertOne(doc);

        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        assertTrue(bean.isAuthorized(userId, token));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000001"), token));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000000"), "WRONG TOKEN!!!"));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000000"), ""));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000001"), "WRONG TOKEN!!!"));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000001"), ""));
        assertFalse(bean.isAuthorized(null, "WRONG TOKEN!!!"));
        assertFalse(bean.isAuthorized(null, ""));
        assertFalse(bean.isAuthorized(null, token));
        assertFalse(bean.isAuthorized(userId, null));
        assertFalse(bean.isAuthorized(new ObjectId("000000000000000000000001"), null));
        assertFalse(bean.isAuthorized(null, null));
    }

    /**
     * Test the {@link UsersBean#removeToken(ObjectId, String)} function with valid and invalid input.
     */
    @Test
    void removeTokenTest() {
        String token = "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";
        ObjectId userId = new ObjectId("000000000000000000000000");
        Document doc = new Document().append("token", token).append("userId", userId);
        container.getCollection("tokens").insertOne(doc);

        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(null, null);
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(userId, null);
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(null, token);
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(null, "WRONG TOKEN!!!");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(null, "");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(userId, "WRONG TOKEN!!!");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(userId, "");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(new ObjectId("000000000000000000000001"), null);
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(new ObjectId("000000000000000000000001"), token);
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(new ObjectId("000000000000000000000001"), "WRONG TOKEN!!!");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(new ObjectId("000000000000000000000001"), "");
        assertEquals(1, container.getCollection("tokens").countDocuments());
        bean.removeToken(userId, token);
        assertEquals(0, container.getCollection("tokens").countDocuments());
    }

    /**
     * Get a user with UserId equals null.
     */
    @Test
    void getUserNullTest() {
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        final User[] user = new User[1];
        assertThrows(NullPointerException.class, () -> user[0] = bean.getUser(null));
        assertNull(user[0]);
    }

    /**
     * Test to get a non existing user.
     */
    @Test
    void getUserNotExistingTest() {
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        User user = bean.getUser(new ObjectId());
        assertNull(user);
    }

    /**
     * Test to get a existing User.
     */
    @Test
    void getUserExistingTest() {
        insertUsers();
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        var user1 = bean.getUser(new ObjectId("000000000000000000000000"));
        assertNotNull(user1);
        assertEquals("000000000000000000000000", user1.getID().toHexString());
        assertEquals("bla@blabla.de", user1.getEmail());
        assertEquals("blabla", user1.getUsername());
        assertEquals(UsersBean.sha512("pass1234"), user1.getPassword());

        var user2 = bean.getUser(new ObjectId("000000000000000000000001"));
        assertNotNull(user2);
        assertEquals("000000000000000000000001", user2.getID().toHexString());
        assertEquals("nanana@nanana.de", user2.getEmail());
        assertEquals("nanana", user2.getUsername());
        assertEquals(UsersBean.sha512("nanapass"), user2.getPassword());

        var user3 = bean.getUser(new ObjectId("000000000000000000000002"));
        assertNotNull(user3);
        assertEquals("000000000000000000000002", user3.getID().toHexString());
        assertEquals("example@example.de", user3.getEmail());
        assertEquals("example", user3.getUsername());
        assertEquals(UsersBean.sha512("examplepass"), user3.getPassword());

        var user4 = bean.getUser(new ObjectId("000000000000000000000003"));
        assertNotNull(user4);
        assertEquals("000000000000000000000003", user4.getID().toHexString());
        assertEquals("king@kong.de", user4.getEmail());
        assertEquals("king", user4.getUsername());
        assertEquals(UsersBean.sha512("kingpass"), user4.getPassword());
    }

    /**
     * Test the {@link UsersBean#getUser(ObjectId)} function with existing and not existing UserIds.
     */
    @Test
    void getUserNameTest() {
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        Map<String, ObjectId> users = insertUsers();
        for(String username : users.keySet()) {
            assertEquals(username, bean.getUsername(users.get(username)));
        }
        assertThrows(IncompleteRequestException.class, () -> bean.getUsername(null));
        assertThrows(UserNotFoundException.class, () -> bean.getUsername(new ObjectId("000000000000000000000004")));
        assertThrows(UserNotFoundException.class, () -> bean.getUsername(new ObjectId("000000000000000000000005")));
    }

    /**
     * Test the {@link UsersBean#checkAuthorized(ObjectId)} function with valid and invalid UserIds.
     */
    @Test
    void checkAuthorizedTest() {
        UsersBean bean = new UsersBean(container.getMongoClient(), new SerializerBean());
        Map<String, ObjectId> users = insertUsers();
        for(ObjectId user : users.values()) {
            bean.checkAuthorized(user);
        }
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(null));
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(new ObjectId("000000000000000000000004")));
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(new ObjectId("000000000000000000000005")));
    }
}


