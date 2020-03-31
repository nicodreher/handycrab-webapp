package de.dhbw.handycrab.server.beans.persistence;

import org.bson.conversions.Bson;

/**
 * The RequestBuilder is used to define a new database request
 *
 * @author Nico Dreher
 */
public class RequestBuilder<T> implements Cloneable {

  private Class<T> type;
  /**
   * The Bson object to filter the documents
   */
  private Bson filter;
  /**
   * The Bson object to define the sort direction an values of the documents
   */
  private Bson sort;
  /**
   * The amount of documents to request
   */
  private int limit;
  /**
   * The indicator if a limit is set
   */
  private boolean limitSet = false;
  /**
   * The amount of documents to skip in the request
   */
  private int offset;
  /**
   * The indicator if a offset is set
   */
  private boolean offsetSet = false;

  public RequestBuilder() {

  }

  public RequestBuilder(Class<T> tClass) {
    type = tClass;
  }

  public RequestBuilder<T> filter(Bson filter) {
    this.filter = filter;
    return this;
  }

  public Bson getFilter() {
    return filter;
  }

  public RequestBuilder<T> sort(Bson sort) {
    this.sort = sort;
    return this;
  }

  public Bson getSort() {
    return sort;
  }

  public RequestBuilder<T> limit(int limit) {
    limitSet = true;
    this.limit = limit;
    return this;
  }

  public int getLimit() {
    return limit;
  }

  public boolean isLimitSet() {
    return limitSet;
  }

  public RequestBuilder<T> offset(int offset) {
    offsetSet = true;
    this.offset = offset;
    return this;
  }

  public int getOffset() {
    return offset;
  }

  public boolean isOffsetSet() {
    return offsetSet;
  }

  public Class<T> getType() {
    return type;
  }

  @Override
  public RequestBuilder<T> clone() {
    RequestBuilder<T> builder = new RequestBuilder<T>().filter(getFilter()).sort(getSort());
    if (isLimitSet()) {
      builder.limit(getLimit());
    }
    if (isOffsetSet()) {
      builder.offset(getOffset());
    }
    return builder;
  }
}
