package de.dhbw.handycrab.server.beans;

import com.google.gson.Gson;
import de.dhbw.handycrab.api.Test;
import de.dhbw.handycrab.server.utils.GsonUtils;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Remote(Test.class)
@Startup
@Singleton
public class TestBean<T> implements Test<T> {

    Random rdm = new Random();
  int value;
  Gson gson = GsonUtils.getGson();

    @PostConstruct
    private void postConstruct() {
        value = rdm.nextInt();
    }


    @Override
    public T getValue(String value, Class<T> clazz) {
        return gson.fromJson(value, clazz);
    }
}
