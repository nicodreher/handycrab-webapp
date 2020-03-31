package de.dhbw.handycrab.server.beans.users;

import de.dhbw.handycrab.api.users.User;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.server.exceptions.UnauthorizedException;
import org.bson.types.ObjectId;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.UUID;

@Stateless
@Remote(Users.class)
public class UsersBean implements Users {
    @Override
    public User register(String email, String username, String password) {
        return null;
    }

    @Override
    public User login(String login, String password) {
        return null ;
    }

    @Override
    public String getUsername(ObjectId id) {
        return null;
    }

    @Override
    public boolean isAuthorized(ObjectId id) {
        return id != null;
    }

    @Override
    public void checkAuthorized(ObjectId id) {
        if(!isAuthorized(id)) {
            throw new UnauthorizedException();
        }
    }
}
