package de.dhbw.handycrab.api.utils;

public interface Serializer {
    String LOOKUP = "java:app/server/SerializerBean!de.dhbw.handycrab.api.utils.Serializer";

    public <T> T deserialize(String json, Class<T> tClass);

    String serialize(Object obj);

    public <T> T restDeserialize(String json, Class<T> tClass);

    String restSerialize(Object obj);

}
