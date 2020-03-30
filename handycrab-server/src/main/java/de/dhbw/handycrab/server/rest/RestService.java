package de.dhbw.handycrab.server.rest;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import org.bson.Document;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("")
public class RestService {
    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    @GET
    @Path("/check")
    @Produces(MediaType.TEXT_PLAIN)
    public String check() {
        return "true";
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
}
