package de.dhbw.handycrab.server.rest.pictures;

import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.api.pictures.Pictures;
import org.bson.types.ObjectId;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Base64;

/**
 * The REST-Service to retrieve uploaded pictures using the {@link de.dhbw.handycrab.server.beans.pictures.PicturesBean}
 * @author Nico Dreher
 */
@Path("/pictures")
public class PicturesService {
    @Resource(lookup = Pictures.LOOKUP)
    private Pictures pictures;

    @GET
    @Path("{id}")
    public Response getImage(@PathParam("id") String id) {
        Picture picture = pictures.get(new ObjectId());
        return Response.ok().type(picture.getContentType()).entity(Base64.getDecoder().decode(picture.getBase64())).build();
    }
}
