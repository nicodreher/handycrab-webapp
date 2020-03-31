package de.dhbw.handycrab.server.beans.persistence;

import com.google.gson.Gson;
import de.dhbw.handycrab.server.utils.GsonUtils;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

@Stateless
public class SerializerBean {

  private Gson gson;

  @PostConstruct
  public void init() {
    gson = GsonUtils.getGson();
  }

  public <T> T deserialize(String json, Class<T> tClass) {
    return gson.fromJson(json, tClass);
  }

  public String serialize(Object obj) {
    return gson.toJson(obj);
  }

}
