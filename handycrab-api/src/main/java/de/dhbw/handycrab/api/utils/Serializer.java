package de.dhbw.handycrab.api.utils;

/**
 * A EJB-Interface to serialize an deserialize objects with Gson.
 * @author Nico Dreher
 */
public interface Serializer {
    String LOOKUP = "java:app/server/SerializerBean!de.dhbw.handycrab.api.utils.Serializer";

    /**
     * Deserialize a object coming from the database
     * @param json The JSON Object
     * @param tClass The Class of the object
     * @param <T> The Type of the object
     * @return The deserialized object
     */
    <T> T deserialize(String json, Class<T> tClass);

    /**
     * Serialize a object for the database
     * @param obj The object to serialize
     * @return The serialized JSON Object as string
     */
    String serialize(Object obj);

    /**
     * Deserialize a object coming from a REST-Client
     * @param json The JSON Object
     * @param tClass The Class of the object
     * @param <T> The Type of the object
     * @return The deserialized object
     */
    <T> T restDeserialize(String json, Class<T> tClass);

    /**
     * Serialize a object for the REST-Client
     * @param obj The object to serialize
     * @return The serialized JSON Object as string
     */
    String restSerialize(Object obj);

}
