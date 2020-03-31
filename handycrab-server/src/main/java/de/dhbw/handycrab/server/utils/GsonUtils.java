package de.dhbw.handycrab.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The GsonUtils are used to generate the Gson Serializer
 *
 * @author Nico Dreher
 */
public class GsonUtils {
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

    public static Gson getGson() {
        return gsonBuilder.create();
    }

}
