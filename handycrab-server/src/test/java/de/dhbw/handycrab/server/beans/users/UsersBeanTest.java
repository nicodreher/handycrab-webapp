package de.dhbw.handycrab.server.beans.users;
import static org.junit.jupiter.api.Assertions.*;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.FindIterable;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
import de.dhbw.handycrab.server.exceptions.InvalidMailException;
import de.dhbw.handycrab.server.test.mongo.MongoTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class UsersBeanTest extends MongoTest {

    @Test
    public void registerTest() {
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        User user = bean.register("test@test.de", "test", "test1234");
        assertNotNull(user.getID());
        Bson filter = and(eq("email", user.getEmail()), eq("username", user.getUsername()));
        assertEquals(1, getCollection("users").countDocuments(filter));
        FindIterable result = getCollection("users").find(filter);
        Document first = (Document) result.first();
        assertNotNull(first);
        assertEquals(user.getID(), first.getObjectId("_id"));
    }

    @Test
    public void registerWrongEmailTest() {
        String mail = "invalid";
        UsersBean bean = new UsersBean(getMongoClient(), new SerializerBean());
        final User[] user = new User[1];
        assertThrows(InvalidMailException.class, () -> user[0] = bean.register(mail, "test", "test1234"));
        assertNull(user[0]);
        Bson filter = eq("email", mail);
        assertEquals(0, getCollection("users").countDocuments(filter));
    }
}
