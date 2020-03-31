package de.dhbw.handycrab.server.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("")
public class RestApplication extends Application {
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON;
}
