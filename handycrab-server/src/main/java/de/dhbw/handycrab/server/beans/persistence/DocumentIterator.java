package de.dhbw.handycrab.server.beans.persistence;

import com.mongodb.client.MongoCursor;
import java.util.Iterator;
import javax.inject.Inject;
import org.bson.Document;

/**
 * The DocumentIterator provides an Iterator which can convert a MongoCursor of Bson Documents to a
 * Stream of Java objects
 *
 * @param <T> The type of the Java objects
 * @author Nico Dreher
 * @see Iterator
 */
public class DocumentIterator<T> implements Iterator<T> {

  /**
   * The Gson serializer object
   */
  @Inject
  private SerializerBean serializer;
  /**
   * The MongoCursor to iterate over
   */
  private MongoCursor<Document> cursor;
  /**
   * The type of the java object output
   */
  private Class<T> type;

  public DocumentIterator() {
  }

  /**
   * Creates an new DocumentIterator
   *
   * @param type   the type of the java object output
   * @param cursor the MongoCursor to iterate over
   */
  public DocumentIterator(Class<T> type, MongoCursor<Document> cursor) {
    this.type = type;
    this.cursor = cursor;
  }

  /**
   * Check if the Iterator has a value left
   *
   * @return the whether the Iterator has a value left
   * @see Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return cursor.hasNext();
  }

  /**
   * Get the next value
   *
   * @return the next value
   * @see Iterator#next()
   */
  @Override
  public T next() {
    return fromBson(cursor.next());
  }

  /**
   * Convert a Bson Document to a Java object using Gson
   *
   * @param document the document to convert
   * @return the Java object
   */
  protected T fromBson(Document document) {
    return document != null ? serializer.deserialize(document.toJson(), type) : null;
  }
}
