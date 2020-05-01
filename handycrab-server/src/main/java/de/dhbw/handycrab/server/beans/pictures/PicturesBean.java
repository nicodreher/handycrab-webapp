package de.dhbw.handycrab.server.beans.pictures;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.api.pictures.Pictures;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.print.Doc;
import java.util.UUID;

@Stateless
@Remote(Pictures.class)
public class PicturesBean implements Pictures {
    private static final ObjectId example = new ObjectId("5e8350d411c3931b216a67a4");
    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    public PicturesBean() {

    }

    public PicturesBean(MongoClient client) {
        this.client = client;
    }

    @Override
    public Picture get(ObjectId uuid) {
        //TODO: Nicer Implementation
        Document doc = client.getDatabase(System.getenv("mongo_database")).getCollection("pictures").find(Filters.eq("_id", example)).first();
        return new Picture(doc.getString("base64"), doc.getString("contentType"));
    }

    @Override
    public Picture put(String base64) {
        return null;
    }
}
