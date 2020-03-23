package de.dhbw.handycrab.server.beans;

import de.dhbw.handycrab.api.Test;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Random;

@Remote(Test.class)
@Startup
@Singleton
public class TestBean implements Test {

    Random rdm = new Random();
    int value;

    @PostConstruct
    private void postConstruct() {
        value = rdm.nextInt();
    }

    @Override
    public int getValue() {
        return value;
    }
}
