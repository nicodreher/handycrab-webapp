package de.dhbw.handycrab.server.rest.barriers;


import de.dhbw.handycrab.api.RequestResult;
import de.dhbw.handycrab.api.barriers.Barriers;
import de.dhbw.handycrab.api.barriers.FrontendBarrier;
import de.dhbw.handycrab.api.barriers.Vote;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.server.rest.authorization.Authorized;
import de.dhbw.handycrab.server.rest.authorization.CurrentUser;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.stream.Collectors;

import static de.dhbw.handycrab.server.rest.RestApplication.*;

/**
 * REST-Service for handling barriers using {@link de.dhbw.handycrab.server.beans.barriers.BarriersBean}
 *
 * @author Lukas Lautenschlager
 */
@Path("/barriers")
public class BarriersService {
    @Resource(lookup = Barriers.LOOKUP)
    private Barriers barriers;

    @Inject
    @CurrentUser
    private User user;

    @GET
    @Authorized
    @Path("/get")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public Object get(String json) {
        var obj = new JSONObject(json);
        var userId = user.getID();
        if(obj.has("_id")) {
            return new FrontendBarrier(barriers.getBarrier(new ObjectId(validateObjectId(obj.getString("_id")))),
                    userId);
        }
        else if(obj.has("postcode")) {
            return barriers.getBarrier(obj.getString("postcode"))
                    .stream().map(e -> new FrontendBarrier(e, userId)).collect(Collectors.toList());
        }
        else if(obj.has("longitude") && obj.has("latitude") && obj.has("radius")) {
            return barriers.getBarrier(obj.getDouble("longitude"), obj.getDouble("latitude"), obj.getInt("radius"),
                    obj.optBoolean("toDelete", false))
                    .stream().map(e -> new FrontendBarrier(e, userId)).collect(Collectors.toList());
        }
        else {
            return barriers.getBarrierOnUserId(userId).stream().map(e -> new FrontendBarrier(e, userId))
                    .collect(Collectors.toList());
        }
    }

    @GET
    @Authorized
    @Path("/get")
    @Produces(MEDIA_TYPE)
    public Object get(@QueryParam("_id") String id, @QueryParam("postcode") String postcode,
            @QueryParam("longitude") Double longitude, @QueryParam("latitude") Double latitude,
            @QueryParam("radius") Integer radius, @QueryParam("toDelete") boolean toDelete) {
        if(id != null && !id.isEmpty()) {
            return new FrontendBarrier(barriers.getBarrier(new ObjectId(validateObjectId(id))), user.getID());
        }
        else if(postcode != null && !postcode.isEmpty()) {
            return barriers.getBarrier(postcode).stream().map(e -> new FrontendBarrier(e, user.getID()))
                    .collect(Collectors.toList());
        }
        else if(longitude != null && latitude != null && radius != null) {
            return barriers.getBarrier(longitude, latitude, radius, toDelete).stream()
                    .map(e -> new FrontendBarrier(e, user.getID())).collect(Collectors.toList());
        }
        else if(longitude == null && latitude == null && radius == null) {
            return barriers.getBarrierOnUserId(user.getID()).stream().map(e -> new FrontendBarrier(e, user.getID()))
                    .collect(Collectors.toList());
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @POST
    @Authorized
    @Path("/add")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addBarrier(String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(barriers.addBarrier(obj.optString("title", null), obj.optDouble("longitude", 200),
                obj.optDouble("latitude", 100), obj.optString("picture", null), obj.optString("postcode", null),
                obj.optString("description", null), obj.optString("solution", null), user.getID()), user.getID());
    }

    @PUT
    @Authorized
    @Path("/modify")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier modifyBarrier(String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(barriers.modifyBarrier(new ObjectId(validateObjectId(obj.optString("_id", null))),
                obj.optString("title", null), obj.optString("picture", null), obj.optString("description", null),
                user.getID()), user.getID());
    }

    @POST
    @Authorized
    @Path("/solution")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addSolution(String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(barriers.addSolution(new ObjectId(validateObjectId(obj.getString("_id"))),
                obj.optString("solution", null), user.getID()), user.getID());
    }

    @PUT
    @Authorized
    @Path("/vote")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addVote(String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(barriers.addVoteToBarrier(new ObjectId(validateObjectId(obj.optString("_id", null))),
                Vote.valueOf(obj.optString("vote", null)), user.getID()), user.getID());
    }

    @PUT
    @Authorized
    @Path("/solutions/vote")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addVoteToSolution(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(
                barriers.addVoteToSolution(new ObjectId(validateObjectId(obj.optString("_id", null))),
                        Vote.valueOf(obj.optString("vote", null)), user.getID()), user.getID());
    }

    @DELETE
    @Authorized
    @Path("/delete")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public RequestResult deleteBarrier(String json) {
        JSONObject obj = new JSONObject(json);
        return new RequestResult(
                barriers.deleteBarrier(new ObjectId(validateObjectId(obj.optString("_id", null))), user.getID()));
    }

    @DELETE
    @Authorized
    @Path("/mark")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public RequestResult markBarrier(String json) {
        JSONObject obj = new JSONObject(json);
        return new RequestResult(
                barriers.markBarrierForDeletion(new ObjectId(validateObjectId(obj.optString("_id", null))),
                        user.getID()));
    }

    @POST
    @Authorized
    @Path("/comment")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addCommentToBarrier(String json) {
        JSONObject obj = new JSONObject(json);
        return new FrontendBarrier(
                barriers.addCommentToBarrier(new ObjectId(validateObjectId(obj.optString("_id", null))),
                        obj.optString("comment", null), user.getID()), user.getID());
    }
}
