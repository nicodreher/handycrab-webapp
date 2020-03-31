package de.dhbw.handycrab.api.users;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    public UUID _id;
    public String username;
    public String email;
}
