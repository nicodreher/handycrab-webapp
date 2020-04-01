package de.dhbw.handycrab.server.test.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoTest {

    @Container
    protected MongoContainer container = new MongoContainer();

    private MongoClient client;

    protected MongoClient getMongoClient() {
        if(client == null) {
            client = new MongoClient(container.getContainerIpAddress(), container.getPort());
        }
        return client;
    }

    protected MongoDatabase getMongoDatabase() {
        return getMongoClient().getDatabase(System.getenv("mongo_database"));
    }

    protected MongoCollection<Document> getCollection(String collection) {
        return getMongoDatabase().getCollection(collection);
    }
}
