package de.dhbw.handycrab.server.beans.users;
import static org.junit.jupiter.api.Assertions.*;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.FindIterable;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.server.exceptions.InvalidLoginException;
import de.dhbw.handycrab.server.exceptions.UnauthorizedException;
import de.dhbw.handycrab.server.exceptions.UserNotFoundException;
import de.dhbw.handycrab.server.test.mongo.MongoTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UsersBeanTest extends MongoTest {

    private Document generateUser(ObjectId id, String email, String username, String password) {
        Document doc = new Document();
        doc.put("_id", id);
        doc.put("email", email.toLowerCase());
        doc.put("username", username.toLowerCase());
        doc.put("password", UsersBean.sha512(password));
        return doc;
    }

    private Document[] generateUsers() {
        return new Document[]{generateUser(new ObjectId("000000000000000000000000"), "bla@blabla.de", "blabla", "pass1234"),
                generateUser(new ObjectId("000000000000000000000001"), "nanana@nanana.de", "nanana", "nanapass"),
                generateUser(new ObjectId("000000000000000000000002"), "example@example.de", "example", "examplepass"),
                generateUser(new ObjectId("000000000000000000000003"), "king@kong.de", "king", "kingpass")};
    }

    public Map<String, ObjectId> insertUsers() {
        Document[] docs = generateUsers();
        Map<String, ObjectId> users = new HashMap<>();
        for(Document doc : docs) {
            getCollection("users").insertOne(doc);
            users.put(doc.getString("username"), doc.getObjectId("_id"));
        }
        return users;
    }

    @ParameterizedTest(name = "[{index}] Email: {0} Username: {1} Password: {2}")
    @CsvFileSource(resources = "/users/register.csv")
    public void registerTest(String email, String username, String password) {
        insertUsers();
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        User user = bean.register(email, username, password);
        assertNotNull(user.getID());
        Bson filter = and(eq("email", user.getEmail().toLowerCase()), eq("username", user.getUsername().toLowerCase()));
        assertEquals(1, getCollection("users").countDocuments(filter));
        FindIterable<Document> result = getCollection("users").find(filter);
        Document first = result.first();
        assertNotNull(first);
        assertEquals(user.getID(), first.getObjectId("_id"));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @CsvFileSource(resources = "/users/failregister.csv")
    public void registerWrongEmailTest(String comment, String email, String username, String password, String expectedException) {
        Map<String, ObjectId> users = insertUsers();
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        final User[] user = new User[1];
        try {
            assertThrows((Class<? extends Throwable>) Class.forName(expectedException) , () -> user[0] = bean.register(email, username, password));
        }
        catch (ClassNotFoundException | ClassCastException e) {
            fail(e);
        }
        assertNull(user[0]);
        Bson filter = or(eq("email", email.toLowerCase()), eq("username", username.toLowerCase()));
        if(getCollection("users").countDocuments(filter) != 0) {
            getCollection("users").find(filter).forEach((Consumer<Document>) doc -> {
                assertTrue(users.containsKey(doc.getString("username")));
                assertEquals(users.get(doc.getString("username")), doc.getObjectId("_id"));
            });
        }
    }
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvFileSource(resources = "/users/login.csv")
    public void loginTest(String comment, String email, String username, String password, String login, String loginPassword, boolean correct) {
        Document doc = generateUser(new ObjectId(), email, username, password);
        getCollection("users").insertOne(doc);
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        if(correct) {
            User user = bean.login(login, loginPassword);
            assertNotNull(user);
            assertEquals(doc.getObjectId("_id"), user.getID());
        }
        else {
            final User[] user = new User[1];
            assertThrows(InvalidLoginException.class, () -> user[0] = bean.login(login, loginPassword));
            assertNull(user[0]);
        }
    }

    @Test
    public void getUserNullTest() {
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        final User[] user = new User[1];
        assertThrows(NullPointerException.class, () -> user[0] = bean.getUser(null));
        assertNull(user[0]);
    }

    @Test
    public void getUserNotExistingTest() {
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        User user = bean.getUser(new ObjectId());
        assertNull(user);
    }

    @Test
    public void getUserExistingTest() {
        insertUsers();
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
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

    @Test
    public void getUserNameTest() {
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        Map<String, ObjectId> users = insertUsers();
        for(String username : users.keySet()) {
            assertEquals(username, bean.getUsername(users.get(username)));
        }
        assertThrows(IncompleteRequestException.class, () -> bean.getUsername(null));
        assertThrows(UserNotFoundException.class, () -> bean.getUsername(new ObjectId("000000000000000000000004")));
        assertThrows(UserNotFoundException.class, () -> bean.getUsername(new ObjectId("000000000000000000000005")));
    }

    @Test
    public void checkAuthorizedTest() {
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        Map<String, ObjectId> users = insertUsers();
        for(ObjectId user : users.values()) {
            bean.checkAuthorized(user);
        }
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(null));
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(new ObjectId("000000000000000000000004")));
        assertThrows(UnauthorizedException.class, () -> bean.checkAuthorized(new ObjectId("000000000000000000000005")));
    }


}

