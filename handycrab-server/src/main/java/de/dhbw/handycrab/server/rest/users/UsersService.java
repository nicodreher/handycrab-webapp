package de.dhbw.handycrab.server.rest.users;
import de.dhbw.handycrab.api.RequestResult;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.server.exceptions.UnauthorizedException;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static de.dhbw.handycrab.server.rest.RestApplication.MEDIA_TYPE;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.UUID;

@Path("/users")
public class UsersService {
    @Resource(lookup = Users.LOOKUP)
    private Users users;

    @POST
    @Path("/register")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public User register(@Context HttpServletRequest request, JSONObject entity) {
        return users.register(entity.getString("email"), entity.getString("username"), entity.getString("password"));
    }

    @POST
    @Path("/login")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public User login(@Context HttpServletRequest request, JSONObject entity) {
        User user = users.login(entity.getString("login"), entity.getString("password"));
        request.setAttribute("userId", user.getID());
        return user;
    }

    @POST
    @Path("/logout")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public void logout(@Context HttpServletRequest request) {
        request.setAttribute("userId", null);
    }

    @GET
    @Path("/name")
    @Consumes(MEDIA_TYPE)
    @Produces(MEDIA_TYPE)
    public RequestResult getName(JSONObject entity) {
        return new RequestResult(users.getUsername(new ObjectId(entity.getString("_id"))));
    }
}
