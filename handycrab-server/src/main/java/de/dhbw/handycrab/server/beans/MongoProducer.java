package de.dhbw.handycrab.server.beans;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MongoProducer {

  @Produces
  public MongoDatabase createMongoClient() {
    var username = System.getProperty("mongo-user");
    //change from getenv to getProperty
    var password = System.getenv("mongo_handycrab");
    var host = System.getProperty("mongo-host");
    var database = System.getProperty("mongo-database");
    var port = System.getProperty("mongo-port");
    try {
      MongoCredential cred = MongoCredential
          .createCredential(username, database, password.toCharArray());
      MongoClient client = new MongoClient(new ServerAddress(host, Integer.getInteger(port)),
          Arrays.asList(cred));
      return client.getDatabase(database);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
