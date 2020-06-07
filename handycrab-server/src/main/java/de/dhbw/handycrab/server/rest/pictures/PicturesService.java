package de.dhbw.handycrab.server.rest.pictures;

import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.api.pictures.Pictures;
import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import org.bson.types.ObjectId;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Base64;

/**
 * The REST-Service to retrieve uploaded pictures using the {@link de.dhbw.handycrab.server.beans.pictures.PicturesBean}
 *
 * @author Nico Dreher
 */
@Path("/pictures")
public class PicturesService {
    @Resource(lookup = Pictures.LOOKUP)
    private Pictures pictures;

    @GET
    @Path("{id}")
    public Response getImage(@PathParam("id") String id) {
        if(id.matches("^[0-9a-fA-F]+$") && id.length() == 24) {
            Picture picture = pictures.get(new ObjectId(id));
            return Response.ok().type(picture.getContentType()).entity(Base64.getDecoder().decode(picture.getBase64()))
                    .build();
        }
        else {
            throw new IncompleteRequestException();
        }
    }
}
