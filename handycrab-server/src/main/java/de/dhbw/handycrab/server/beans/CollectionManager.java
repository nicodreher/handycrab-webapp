package de.dhbw.handycrab.server.beans;

import com.mongodb.client.MongoCollection;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.Document;

@EJB
@Stateless
public class CollectionManager {

  @Inject
  MongoProducer mongoProducer;

  public MongoCollection<Document> getCollection(String collectionName) {
    return mongoProducer.createMongoClient().getCollection(collectionName);
  }
}
