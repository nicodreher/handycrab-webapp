package de.dhbw.handycrab.server.rest.barriers;


import de.dhbw.handycrab.api.RequestResult;
import de.dhbw.handycrab.api.barriers.Barriers;
import de.dhbw.handycrab.api.barriers.FrontendBarrier;
import de.dhbw.handycrab.api.barriers.Vote;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.server.rest.authorization.Authorized;
import de.dhbw.handycrab.server.rest.authorization.CurrentUser;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import static de.dhbw.handycrab.server.rest.RestApplication.MEDIA_TYPE;

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
    public Object get(@Context HttpServletRequest request, String json) {
        var obj = new JSONObject(json);
        if (obj.has("_id"))
            return barriers.getBarrier(new ObjectId(obj.getString("_id")), user.getID());
        else if (obj.has("postcode"))
            return barriers.getBarrier(obj.getString("postcode"), user.getID());
        else if (obj.has("longitude") && obj.has("latitude") && obj.has("radius"))
            return barriers.getBarrier(obj.getDouble("longitude"), obj.getDouble("latitude"), obj.getInt("radius"), user.getID());
        else
            return barriers.getBarrier(user.getID());
    }

    @POST
    @Authorized
    @Path("/add")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addBarrier(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.addBarrier(obj.optString("title", null), obj.optDouble("longitude", 200), obj.optDouble("latitude", 100), obj.optString("picture", null), obj.optString("postcode", null), obj.optString("description", null), obj.optString("solution", null), user.getID());
    }

    @PUT
    @Authorized
    @Path("/modify")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier modifyBarrier(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.modifyBarrier(new ObjectId(obj.optString("_id", null)), obj.optString("title", null), obj.optString("picture", null), obj.optString("description", null), user.getID());
    }

    @POST
    @Authorized
    @Path("/solution")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addSolution(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.addSolution(new ObjectId(obj.getString("_id")), obj.optString("solution", null), user.getID());
    }

    @PUT
    @Authorized
    @Path("/vote")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addVote(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.addVoteToBarrier(new ObjectId(obj.optString("_id", null)), Vote.valueOf(obj.optString("vote", null)), user.getID());
    }

    @PUT
    @Authorized
    @Path("/solutions/vote")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendBarrier addVoteToSolution(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.addVoteToSolution(new ObjectId(obj.optString("_id", null)), Vote.valueOf(obj.optString("vote", null)), user.getID());
    }

    @DELETE
    @Authorized
    @Path("/delete")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public RequestResult deleteBarrier(@Context HttpServletRequest request, String json) {
        JSONObject obj = new JSONObject(json);
        return barriers.deleteBarrier(new ObjectId(obj.optString("_id", null)), user.getID());
    }
}
