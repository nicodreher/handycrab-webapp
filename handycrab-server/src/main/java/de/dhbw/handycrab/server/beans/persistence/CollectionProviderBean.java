package de.dhbw.handycrab.server.beans.persistence;

import java.util.HashMap;
import javax.ejb.Singleton;

@Singleton
public class CollectionProviderBean {

  private HashMap<Class<?>, String> collectionMapper = new HashMap<>();

  public void registerType(Class<?> type, String collectionName) {
    collectionMapper.put(type, collectionName);
  }

  public String getCollectionName(Class<?> type) {
    return collectionMapper.get(type);
  }
}
