package de.dhbw.handycrab.server.beans.utils;

import com.google.gson.*;
import de.dhbw.handycrab.api.utils.Serializer;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(Serializer.class)
public class SerializerBean implements Serializer {

    private static final GsonBuilder gsonBuilder = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(ObjectId.class, (JsonSerializer<ObjectId>) (src, typeOfSrc, context) -> {
                JsonObject object = new JsonObject();
                object.add("$oid", new JsonPrimitive(src.toHexString()));
                return object;
            })
            .registerTypeAdapter(JSONObject.class,
                    (JsonSerializer<JSONObject>) (src, typeOfSrc, context) -> new JsonParser().parse(src.toString()))
            .registerTypeAdapter(JSONArray.class,
                    (JsonSerializer<JSONArray>) (src, typeOfSrc, context) -> new JsonParser().parse(src.toString()))
            .registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (json, typeOfT, context) -> new ObjectId(
                    json.getAsJsonObject().get("$oid").getAsString()))
            .registerTypeAdapter(JSONObject.class,
                    (JsonDeserializer<JSONObject>) (json, typeofT, context) -> new JSONObject(json.toString()))
            .registerTypeAdapter(JSONArray.class,
                    (JsonDeserializer<JSONArray>) (json, typeofT, context) -> new JSONArray(json.toString()));


    private static final GsonBuilder restGsonBuilder = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(ObjectId.class, (JsonSerializer<ObjectId>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toHexString()))
            .registerTypeAdapter(JSONObject.class,
                    (JsonSerializer<JSONObject>) (src, typeOfSrc, context) -> new JsonParser().parse(src.toString()))
            .registerTypeAdapter(JSONArray.class,
                    (JsonSerializer<JSONArray>) (src, typeOfSrc, context) -> new JsonParser().parse(src.toString()))
            .registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (json, typeOfT, context) -> new ObjectId(json.getAsString()))
            .registerTypeAdapter(JSONObject.class,
                    (JsonDeserializer<JSONObject>) (json, typeofT, context) -> new JSONObject(json.toString()))
            .registerTypeAdapter(JSONArray.class,
                    (JsonDeserializer<JSONArray>) (json, typeofT, context) -> new JSONArray(json.toString()));

    private Gson gson;
    private Gson restGson;

    public SerializerBean() {
        gson = gsonBuilder.create();
        restGson = restGsonBuilder.create();
    }

    @Override
    public <T> T deserialize(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    @Override
    public String serialize(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T restDeserialize(String json, Class<T> tClass) {
        return restGson.fromJson(json, tClass);
    }

    @Override
    public String restSerialize(Object obj) {
        return restGson.toJson(obj);
    }
}
