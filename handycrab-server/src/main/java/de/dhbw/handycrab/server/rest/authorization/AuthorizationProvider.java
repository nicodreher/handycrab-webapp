package de.dhbw.handycrab.server.rest.authorization;

import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import org.bson.types.ObjectId;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Provides the {@link CurrentUser} Fields an the {@link Authorized} annotated functions.
 * @author Nico Dreher
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationProvider implements ContainerRequestFilter {

    @Resource(lookup = Users.LOOKUP)
    private Users users;

    @Context
    private HttpServletRequest request;

    @Context
    private ResourceInfo info;

    @Produces
    @CurrentUser
    public User getCurrentUser() {
        ObjectId userId = (ObjectId) request.getSession().getAttribute("userId");
        if(userId != null) {
            System.out.println("GetUser " + userId);
            return users.getUser(userId);
        }
        return null;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(info.getResourceClass().isAnnotationPresent(Authorized.class) || info.getResourceMethod().isAnnotationPresent(Authorized.class)) {
            users.checkAuthorized((ObjectId) request.getSession().getAttribute("userId"));
        }
    }
}
