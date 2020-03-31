package de.dhbw.handycrab.api;

public interface Test<T> {
    String LOOKUP = "java:app/server/TestBean!de.dhbw.handycrab.api.Test";
    T getValue(String value, Class<T> clazz);
}
