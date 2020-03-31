package de.dhbw.handycrab.server.beans;

import com.google.gson.Gson;
import de.dhbw.handycrab.api.Test;
import de.dhbw.handycrab.server.rest.serialization.GsonProvider;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Random;

@Remote(Test.class)
@Startup
@Singleton
public class TestBean<T> implements Test<T> {

    Random rdm = new Random();
    int value;
    Gson gson = de.dhbw.handycrab.api.utils.GsonUtils.getGson();

    @PostConstruct
    private void postConstruct() {
        value = rdm.nextInt();
    }


    @Override
    public T getValue(String value, Class<T> clazz) {
        return gson.fromJson(value, clazz);
    }
}
