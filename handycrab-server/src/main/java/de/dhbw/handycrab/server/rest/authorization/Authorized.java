package de.dhbw.handycrab.server.rest.authorization;

import javax.ws.rs.core.Context;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Annotation to make a REST-Function only acessable by logged in users
 * @author Nico Dreher
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorized {
}
