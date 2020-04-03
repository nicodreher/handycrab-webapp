package de.dhbw.handycrab.server.test.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;

public class MongoContainer extends GenericContainer<MongoContainer> {

    /**
     * This is the internal port on which MongoDB is running inside the container.
     * <p>
     * You can use this constant in case you want to map an explicit public port to it
     * instead of the default random port. This can be done using methods like
     * {@link #setPortBindings(java.util.List)}.
     */
    public static final int MONGODB_PORT = 27017;
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:latest";
    private MongoClient client;

    /**
     * Creates a new {@link MongoContainer} with the {@value DEFAULT_IMAGE_AND_TAG} image.
     */
    public MongoContainer() {
        this(DEFAULT_IMAGE_AND_TAG);
    }

    /**
     * Creates a new {@link MongoContainer} with the given {@code 'image'}.
     *
     * @param image the image (e.g. {@value DEFAULT_IMAGE_AND_TAG}) to use
     */
    public MongoContainer(@NotNull String image) {
        super(image);
        addExposedPort(MONGODB_PORT);
    }

    /**
     * Returns the actual public port of the internal MongoDB port ({@value MONGODB_PORT}).
     *
     * @return the public port of this container
     * @see #getMappedPort(int)
     */
    @NotNull
    public Integer getPort() {
        return getMappedPort(MONGODB_PORT);
    }

    public MongoClient getMongoClient() {
        if(client == null) {
            client = new MongoClient(getContainerIpAddress(), getPort());
        }
        return client;
    }

    public MongoDatabase getMongoDatabase() {
        return getMongoClient().getDatabase(System.getenv("mongo_database"));
    }

    public MongoCollection<Document> getCollection(String collection) {
        return getMongoDatabase().getCollection(collection);
    }

}