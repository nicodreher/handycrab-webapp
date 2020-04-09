package de.dhbw.handycrab.server.rest;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import de.dhbw.handycrab.api.Test;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.dhbw.handycrab.server.rest.authorization.Authorized;
import de.dhbw.handycrab.server.rest.authorization.CurrentUser;
import org.bson.Document;

@Path("")
public class RestService {
    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;
    @Resource(lookup = Test.LOOKUP)
    private Test<User> test;
    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    @Inject
    @CurrentUser
    private User currentUser;

    @GET
    @Path("/check")
    @Produces(MediaType.TEXT_PLAIN)
    public String check() {
        User user = new User("King", "King@Kong.com", "test");
        return "true" + test.getValue(serializer.serialize(user), User.class);
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Auch true2";
    }

    @GET
    @Path("/serverexception")
    @Produces(MediaType.APPLICATION_JSON)
    public String serverException() {
        throw new NullPointerException();
    }

    @GET
    @Path("/incompleteexception")
    @Produces(MediaType.APPLICATION_JSON)
    public String incompleteException() {
        throw new IncompleteRequestException();
    }

    @GET
    @Path("/messages")
    @Produces(MediaType.TEXT_HTML)
    public String messages() {
        MongoCollection<?> collection = client.getDatabase(System.getenv("mongo_database")).getCollection("messages");
        long offset = collection.countDocuments() - 100;
        List<String> list = new ArrayList<>();
        for (Object obj : collection.find().sort(new Document("date", -1)).limit(100).skip((int) (offset >= 100 ? offset - 100 : 0))) {
            if (obj instanceof Document) {
                Document doc = (Document) obj;
                list.add(doc.getString("sender") + ":" + doc.getString("message"));
            }
        }

        return "<html><body>" + list.stream().map(msg -> {
            String[] msgs = msg.split(":");
            return  "<b>" + msgs[0] + ":</b> " + msgs[1];
        }).collect(Collectors.joining("<br />")) + "</body></html>";
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser() {
        User user = new User("King", "King@Kong.com", "test");
        return user;
    }

    @GET
    @Path("/authorized")
    @Authorized
    public Response authorized() {
        return Response.ok().build();
    }

    @GET
    @Path("/currentuser")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCurrentUser() {
        return currentUser != null ? currentUser.getUsername() : "null";
    }
}
