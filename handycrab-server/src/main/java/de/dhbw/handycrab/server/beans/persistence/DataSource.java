package de.dhbw.handycrab.server.beans.persistence;

import com.google.gson.JsonSyntaxException;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import java.lang.reflect.Field;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * The DataSources are used to store and find serialized Java objects in a MongoDB Collection
 *
 * @param <T> the type of the objects
 * @author Nico Dreher
 */
@Stateless
public class DataSource<T> {

  @EJB
  CollectionProviderBean collectionProviderBean;
  @Resource(lookup = "java:global/MongoClient")
  private MongoClient mongoClient;
  @EJB
  private SerializerBean serializerBean;

  /**
   * Check if the Collection contains a document with the ID
   *
   * @param _id the id of the document
   * @return whether the Collection contains the document
   */
  public boolean contains(Object _id, String collectionName) {
    return contains(new Document("_id", _id), collectionName);
  }

  /**
   * Check if the Collection contains a document which matches a filter
   *
   * @param filter the filter to match
   * @return wheater the Collection contains a document which matches the filter
   */
  public boolean contains(Bson filter, String collectionName) {
    return getCollection(collectionName).countDocuments(filter) > 0;
  }

  /**
   * Get all documents of the Collection as a Stream of Java objects
   *
   * @return the Stream of Java objects
   */
  public Stream<T> find(Class<T> type) {
    MongoCursor<Document> iterator = getCollection(collectionProviderBean.getCollectionName(type))
        .find().iterator();
    return StreamSupport.stream(Spliterators
            .spliteratorUnknownSize(new DocumentIterator<>(type, iterator), Spliterator.DISTINCT),
        false);
  }

  /**
   * Get a FindIterable of Documents with a RequestBuilder
   *
   * @param builder the Database RequestBuilder
   * @return the Iterable
   */
  private FindIterable<Document> getIterable(RequestBuilder<T> builder) {
    FindIterable<Document> iterable;
    if (builder.getFilter() != null) {
      iterable = getCollection(collectionProviderBean.getCollectionName(builder.getType()))
          .find(builder.getFilter());
    } else {
      iterable = getCollection(collectionProviderBean.getCollectionName(builder.getType())).find();
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
  public Stream<T> find(RequestBuilder<T> builder) {
    FindIterable<Document> iterable = getIterable(builder);

    return StreamSupport.stream(Spliterators
            .spliteratorUnknownSize(new DocumentIterator<T>(builder.getType(), iterable.iterator()),
                Spliterator.DISTINCT),
        false);
  }

  /**
   * Find the first document in the Collection with a RequestBuilder
   *
   * @param builder the Database RequestBuilder
   * @return the first document as a Java object
   */
  public T findFirst(RequestBuilder<T> builder) {
    FindIterable<Document> iterable = getIterable(builder);
    return fromBson(iterable.first(), builder.getType());
  }

  /**
   * Get a document with the given ID as a Java object
   *
   * @param _id the ID of the document
   * @return the document as a Java object
   */
  public T get(Object _id, Class<T> type) {
    return fromBson(
        getCollection(collectionProviderBean.getCollectionName(type)).find(new Document("_id", _id))
            .first(), type);
  }

  /**
   * Insert a object into the Collection
   *
   * @param t the object to insert
   */
  public void insert(T t) {
    Document doc = toBson(t);
    getCollection(collectionProviderBean.getCollectionName(t.getClass())).insertOne(doc);

    if (doc.containsKey("_id")) {
      try {
        Field field = t.getClass().getDeclaredField("_id");
        field.setAccessible(true);
        try {
          field.set(t, serializerBean.deserialize(doc.get("_id").toString(), field.getType()));
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
      getCollection(collectionProviderBean.getCollectionName(t.getClass()))
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
    if (document.containsKey("_id")) {
      getCollection(collectionProviderBean.getCollectionName(t.getClass()))
          .updateOne(new Document("_id", document.get("_id")), new Document("$set", document),
              new UpdateOptions().upsert(true));
    } else {
      insert(t);
    }
  }

  public void deleteOne(Object _id, Class<T> type) {
    deleteOne(Filters.eq("_id", _id), type);
  }

  public void deleteOne(Bson filter, Class<T> type) {
    getCollection(collectionProviderBean.getCollectionName(type)).findOneAndDelete(filter);
  }

  public MongoCollection<Document> getCollection(String collectionName) {
    return mongoClient.getDatabase(System.getenv("mongo_database")).getCollection(collectionName);
  }

  /**
   * Deserialize a object from a Bson Document
   *
   * @param document the document to deserialize
   * @return the object
   */
  protected T fromBson(Document document, Class<T> type) {
    return document != null ? serializerBean.deserialize(document.toJson(), type) : null;
  }

  /**
   * Serialize a object to a Bson Document
   *
   * @param object the object to serialize
   * @return the Bson Document
   */
  protected Document toBson(T object) {
    return Document.parse(serializerBean.serialize(object));
  }
}