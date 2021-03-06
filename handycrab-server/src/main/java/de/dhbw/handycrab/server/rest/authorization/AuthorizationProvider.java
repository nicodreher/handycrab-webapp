package de.dhbw.handycrab.server.rest.authorization;

import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.exceptions.UnauthorizedException;
import org.bson.types.ObjectId;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * Provides the {@link CurrentUser} Fields an the {@link Authorized} annotated functions
 *
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

    /**
     * Checks if the request contains a valid TOKEN cookie. Authorizes the user if this is the case.
     */
    private void checkTokenCookie() {
        if(request.getSession().getAttribute("userId") == null && request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equalsIgnoreCase("TOKEN")) {
                    String[] parts = cookie.getValue().split(":");
                    if(parts.length == 2) {
                        ObjectId userId = new ObjectId(parts[0]);
                        if(users.isAuthorized(userId, parts[1])) {
                            request.getSession().setAttribute("userId", userId);
                        }
                    }
                }
            }
        }
    }

    /**
     * Provides the {@link CurrentUser} annotated fields
     *
     * @return the currently authorized user
     */
    @Produces
    @CurrentUser
    public User getCurrentUser() {
        checkTokenCookie();
        ObjectId userId = (ObjectId) request.getSession().getAttribute("userId");
        if(userId != null) {
            return users.getUser(userId);
        }
        return null;
    }

    /**
     * Filters the requests to {@link Authorized} annotated functions and classes
     *
     * @param requestContext
     * @throws UnauthorizedException If a user tries to access a annotated function or class without being authorized
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws UnauthorizedException {
        checkTokenCookie();
        if(info.getResourceClass().isAnnotationPresent(Authorized.class) ||
                info.getResourceMethod().isAnnotationPresent(Authorized.class)) {
            users.checkAuthorized((ObjectId) request.getSession().getAttribute("userId"));
        }
    }
}
