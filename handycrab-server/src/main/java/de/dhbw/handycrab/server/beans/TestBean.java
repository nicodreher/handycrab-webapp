package de.dhbw.handycrab.server.beans;

import com.google.gson.Gson;
import de.dhbw.handycrab.api.Test;
import de.dhbw.handycrab.api.utils.Serializer;

import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Remote(Test.class)
@Startup
@Singleton
public class TestBean<T> implements Test<T> {

    Random rdm = new Random();
    int value;

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;


    @PostConstruct
    private void postConstruct() {
        value = rdm.nextInt();
    }


    @Override
    public T getValue(String value, Class<T> clazz) {
        return serializer.deserialize(value, clazz);
    }
}
