package de.dhbw.handycrab.server.beans.pictures;

import com.mongodb.MongoClient;
import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.api.pictures.Pictures;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.server.beans.persistence.DataSource;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.server.exceptions.pictures.InvalidPictureFormatException;
import de.dhbw.handycrab.server.exceptions.pictures.PictureNotFoundException;
import de.dhbw.handycrab.server.exceptions.pictures.PictureToBigException;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

@Stateless
@Remote(Pictures.class)
public class PicturesBean implements Pictures {
    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    private DataSource<Picture> dataSource;

    public PicturesBean() {

    }

    public PicturesBean(MongoClient client, Serializer serializer) {
        this.serializer = serializer;
        this.client = client;
        construct();
    }

    @PostConstruct
    private void construct() {
        dataSource = new DataSource<>(Picture.class, "pictures", serializer, client);
    }

    @Override
    public Picture get(ObjectId uuid) {
        if (uuid != null) {
            if (dataSource.contains(uuid))
                return dataSource.get(uuid);
            else
                throw new PictureNotFoundException();
        } else
            throw new IncompleteRequestException();
    }

    @Override
    public Picture put(String base64) {
        if (base64 != null) {
            var decodedBase64 = Base64.getDecoder().decode(base64.getBytes());
            if (decodedBase64.length > 8388608)
                throw new PictureToBigException();
            try {
                var iis = ImageIO.createImageInputStream(new ByteArrayInputStream(decodedBase64));
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                String format = "";
                if (readers.hasNext()) {
                    var reader = readers.next();
                    format = reader.getFormatName();
                }
                if (format.equals("JPEG"))
                    format = "image/jpeg";
                else if (format.equals("png"))
                    format = "image/png";
                else
                    throw new InvalidPictureFormatException();
                var pic = new Picture(base64, format);
                dataSource.insert(pic);
                return pic;
            } catch (IOException e) {
                throw new InvalidPictureFormatException();
            }
        } else
            throw new IncompleteRequestException();
    }
}
