package de.dhbw.handycrab.server.rest.users;
import de.dhbw.handycrab.api.RequestResult;
import de.dhbw.handycrab.api.users.FrontendUser;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.server.exceptions.UnauthorizedException;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static de.dhbw.handycrab.server.rest.RestApplication.MEDIA_TYPE;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/users")
public class UsersService {
    @Resource(lookup = Users.LOOKUP)
    private Users users;

    @POST
    @Path("/register")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendUser register(@Context HttpServletRequest request, String json) {
        JSONObject entity = new JSONObject(json);
        User user = users.register(entity.optString("email", null), entity.optString("username", null), entity.optString("password", null));
        request.getSession().setAttribute("userId", user.getID());
        return new FrontendUser(user);
    }

    @POST
    @Path("/login")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public FrontendUser login(@Context HttpServletRequest request, String json) {
        JSONObject entity = new JSONObject(json);
        User user = users.login(entity.optString("login", null), entity.optString("password", null));
        request.getSession().setAttribute("userId", user.getID());
        return new FrontendUser(user);
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        request.getSession().setAttribute("userId", null);
        return Response.ok().build();
    }

    @GET
    @Path("/name")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public RequestResult getName(String json) {
        JSONObject entity = new JSONObject(json);
        return new RequestResult(users.getUsername(new ObjectId(entity.optString("_id", null))));
    }
}
