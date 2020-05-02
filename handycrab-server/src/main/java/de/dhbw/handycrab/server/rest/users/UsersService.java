package de.dhbw.handycrab.server.rest.users;
import de.dhbw.handycrab.api.RequestResult;
import de.dhbw.handycrab.api.users.FrontendUser;
import de.dhbw.handycrab.api.users.LoggedInUser;
import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.server.rest.authorization.Authorized;
import de.dhbw.handycrab.server.rest.authorization.CurrentUser;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static de.dhbw.handycrab.server.rest.RestApplication.MEDIA_TYPE;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * REST-Services for the {@link de.dhbw.handycrab.server.beans.users.UsersBean}
 * @author Nico Dreher
 */
@Path("/users")
public class UsersService {
    @Resource(lookup = Users.LOOKUP)
    private Users users;

    @Inject
    @CurrentUser
    private User currentUser;

    @GET
    @Path("/currentuser")
    @Authorized
    @Produces(MEDIA_TYPE)
    public FrontendUser getCurrentUser() {
        return new FrontendUser(currentUser);
    }
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
    public Response login(@Context HttpServletRequest request, String json) {
        if(request.getSession().getAttribute("userId") != null) {
            logout(request);
        }
        JSONObject entity = new JSONObject(json);
        LoggedInUser loggedInUser = users.login(entity.optString("login", null), entity.optString("password", null), entity.optBoolean("createToken", false));
        request.getSession().setAttribute("userId", loggedInUser.getUser().getID());
        Response.ResponseBuilder builder = Response.ok().entity(new FrontendUser(loggedInUser.getUser()));
        if(loggedInUser.getToken() != null) {
            NewCookie cookie = new NewCookie("TOKEN", loggedInUser.getUser().getID().toHexString() + ":" + loggedInUser.getToken(), null, null, NewCookie.DEFAULT_VERSION, null, 60 * 60 * 24 * 30, null, false, false);
            builder.cookie(cookie);
        }
        return builder.build();
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        request.getSession().setAttribute("userId", null);
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equalsIgnoreCase("TOKEN")) {
                    String[] parts = cookie.getValue().split(":");
                    if(parts.length == 2) {
                        ObjectId userId = new ObjectId(parts[0]);
                        users.removeToken(userId, parts[1]);
                    }
                }
            }
        }
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
