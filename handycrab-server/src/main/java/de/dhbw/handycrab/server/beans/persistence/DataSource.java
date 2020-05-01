package de.dhbw.handycrab.server.beans.persistence;

import com.google.gson.JsonSyntaxException;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import de.dhbw.handycrab.api.utils.Serializer;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The DataSources are used to store and find serialized Java objects in a MongoDB Collection
 *
 * @param <T> the type of the objects
 * @author Nico Dreher
 */
public class DataSource<T> {
    private Class<T> type;
    private String collection;
    private Serializer serializer;
    private MongoClient client;

    public DataSource(Class<T> type, String collection, Serializer serializer, MongoClient client) {
        this.type = type;
        this.collection = collection;
        this.serializer = serializer;
        this.client = client;
    }

    /**
     * Check if the Collection contains a document with the ID
     *
     * @param _id the id of the document
     * @return whether the Collection contains the document
     */
    public boolean contains(Object _id) {
        if(_id != null) {
            return contains(new Document("_id", _id));
        }
        else {
            throw new NullPointerException();
        }
    }

    /**
     * Check if the Collection contains a document which matches a filter
     *
     * @param filter the filter to match
     * @return wheater the Collection contains a document which matches the filter
     */
    public boolean contains(Bson filter) {
        return getCollection().countDocuments(filter) > 0;
    }

    /**
     * Get all documents of the Collection as a Stream of Java objects
     *
     * @return the Stream of Java objects
     */
    public Stream<T> find() {
        MongoCursor<Document> iterator = getCollection()
                .find().iterator();
        return StreamSupport.stream(Spliterators
                        .spliteratorUnknownSize(new DocumentIterator<>(type, iterator, serializer), Spliterator.DISTINCT),
                false);
    }

    /**
     * Get a FindIterable of Documents with a RequestBuilder
     *
     * @param builder the Database RequestBuilder
     * @return the Iterable
     */
    private FindIterable<Document> getIterable(RequestBuilder builder) {
        FindIterable<Document> iterable;
        if (builder.getFilter() != null) {
            iterable = getCollection()
                    .find(builder.getFilter());
        } else {
            iterable = getCollection().find();
        }

        if (builder.getSort() != null) {
            iterable.sort(builder.getSort());
        }

        if (builder.isLimitSet()) {
            iterable.limit(builder.getLimit());
        }

        if (builder.isOffsetSet()) {
            iterable.skip(builder.getOffset());
        }
        return iterable;
    }

    /**
     * Get all documents of the Collection as a Stream of Java objects with a RequestBuilder
     *
     * @param builder the Database RequestBuilder
     * @return the Stream of Java objects
     */
    public Stream<T> find(RequestBuilder builder) {
        FindIterable<Document> iterable = getIterable(builder);

        return StreamSupport.stream(Spliterators
                        .spliteratorUnknownSize(new DocumentIterator<T>(type, iterable.iterator(), serializer),
                                Spliterator.DISTINCT),
                false);
    }

    /**
     * Find the first document in the Collection with a RequestBuilder
     *
     * @param builder the Database RequestBuilder
     * @return the first document as a Java object
     */
    public T findFirst(RequestBuilder builder) {
        FindIterable<Document> iterable = getIterable(builder);
        return fromBson(iterable.first());
    }

    /**
     * Get a document with the given ID as a Java object
     *
     * @param _id the ID of the document
     * @return the document as a Java object
     */
    public T get(Object _id) {
        if(_id != null) {
            return fromBson(getCollection().find(new Document("_id", _id)).first());
        }
        else {
            throw new NullPointerException();
        }
    }

    /**
     * Insert a object into the Collection
     *
     * @param t the object to insert
     */
    public void insert(T t) {
        Document doc = toBson(t);
        getCollection().insertOne(doc);

        if (doc.containsKey("_id")) {
            try {
                Field field = t.getClass().getDeclaredField("_id");
                field.setAccessible(true);
                try {
                    field.set(t, doc.getObjectId("_id"));
                } catch (JsonSyntaxException e) {
                    field.set(t, doc.get("_id"));
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
    }

    /**
     * Replace a object in the Collection
     *
     * @param t the object to update
     */
    public void update(T t) {
        Document document = toBson(t);
        if (document.containsKey("_id")) {
            Object _id = document.get("_id");
            document.remove("_id");
            getCollection()
                    .updateOne(new Document("_id", _id), new Document("$set", document));
        }
    }

    /**
     * Update or insert a object in the collection
     *
     * @param t the object to upsert
     */
    public void upsert(T t) {
        Document document = toBson(t);
        if(document.containsKey("_id")) {
            getCollection()
                    .updateOne(new Document("_id", document.get("_id")), new Document("$set", document),
                            new UpdateOptions().upsert(true));
        }
        else {
            insert(t);
        }
    }

    public void deleteOne(Object _id) {
        if(_id != null) {
            deleteOne(Filters.eq("_id", _id));
        }
        else {
            throw new NullPointerException();
        }
    }

    public void deleteOne(Bson filter) {
        getCollection().findOneAndDelete(filter);
    }

    public MongoCollection<Document> getCollection() {
        return client.getDatabase(System.getenv("mongo_database")).getCollection(collection);
    }

    /**
     * Deserialize a object from a Bson Document
     *
     * @param document the document to deserialize
     * @return the object
     */
    private T fromBson(Document document) {
        return document != null ? serializer.deserialize(document.toJson(), type) : null;
    }

    /**
     * Serialize a object to a Bson Document
     *
     * @param object the object to serialize
     * @return the Bson Document
     */
    private Document toBson(T object) {
        return Document.parse(serializer.serialize(object));
    }
}
