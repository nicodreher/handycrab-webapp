package de.dhbw.handycrab.server.test.beans.pictures;

import static org.junit.jupiter.api.Assertions.*;
import static com.mongodb.client.model.Filters.*;

import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.server.beans.pictures.PicturesBean;
import de.dhbw.handycrab.server.exceptions.pictures.PictureNotFoundException;
import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import org.apache.commons.compress.utils.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Base64;

/**
 * Tests for the {@link PicturesBean} class.
 *
 * @author Nico Dreher
 */
@Testcontainers
class PicturesBeanTest {

    @Container
    private MongoContainer container = new MongoContainer();

    /**
     * Place a picture in the mongodb.
     * @param objectId
     * @param base64 The base64 encoded picture
     * @param contentType The http media type of the picture
     */
    private void placePicture(ObjectId objectId, String base64, String contentType) {
        Document doc = new Document();
        doc.put("_id", objectId);
        doc.put("base64", base64);
        doc.put("contentType", contentType);
        container.getCollection("pictures").insertOne(doc);
    }

    /**
     * Inserts the array of pictures into the mongodb.
     * @param data The array of the picture data
     */
    private void placePictures(Object[][] data) {
        for(Object[] picture : data) {
            placePicture((ObjectId) picture[0], (String) picture[1], (String) picture[2]);
        }
    }

    /**
     * Generates six valid pictures.
     * @return The data of the pictures in a array
     * @throws IOException
     */
    private Object[][] getDemoPictures() throws IOException {
        return new Object[][]{{new ObjectId("000000000000000000000000"), getFileAsBase64("/pictures/images/success/jpeg1.jpg"), "image/jpeg"},
                {new ObjectId("000000000000000000000001"), getFileAsBase64("/pictures/images/success/jpeg2.jpeg"), "image/jpeg"},
                {new ObjectId("000000000000000000000002"), getFileAsBase64("/pictures/images/success/jpeg3.jpg"), "image/jpeg"},
                {new ObjectId("000000000000000000000003"), getFileAsBase64("/pictures/images/success/png1.png"), "image/png"},
                {new ObjectId("000000000000000000000004"), getFileAsBase64("/pictures/images/success/png2.png"), "image/png"},
                {new ObjectId("000000000000000000000005"), getFileAsBase64("/pictures/images/success/png3.png"), "image/png"}};
    }

    /**
     * Generates a base64 String of a file in the package
     * @param path The path to the file in the package
     * @return The file content as base64 String
     * @throws IOException
     */
    private String getFileAsBase64(String path) throws IOException {
        return Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream(path)));
    }

    /**
     * Tests the {@link PicturesBean#put(String)} function with valid images.
     * @param picturePath
     * @param format The http media type of the picture
     * @throws IOException
     */
    @ParameterizedTest(name = "[{index}] Path: {0} Format: {1}")
    @CsvFileSource(resources = "/pictures/put.csv")
    void putTest(String picturePath, String format) throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient());

        String base64 = getFileAsBase64(picturePath);
        Picture picture = bean.put(base64);
        assertNotNull(picture);
        assertEquals(format, picture.getContentType());
        assertNotNull(picture.getID());
        Document doc = container.getCollection("pictures").find(eq("_id", picture.getID())).first();
        assertNotNull(doc);
        assertEquals(base64, doc.getString("base64"));
        assertEquals(format, doc.getString("contentType"));
    }

    /**
     * Tests the {@link PicturesBean#put(String)} function with invalid images.
     * @param picturePath
     * @param expectedException The exception which should be thrown
     * @throws IOException
     * @throws ClassNotFoundException If the Class of the exception cant't be found
     */
    @ParameterizedTest(name = "[{index}] Path: {0} ExpectedException {1}")
    @CsvFileSource(resources = "/pictures/failPut.csv")
    void failPutTest(String picturePath, String expectedException) throws IOException, ClassNotFoundException {
        PicturesBean bean = new PicturesBean(container.getMongoClient());

        String base64 = picturePath != null ? getFileAsBase64(picturePath) : null;
        Class<? extends Throwable> exception = (Class<? extends Throwable>) Class.forName(expectedException);

        final Picture[] picture = new Picture[1];
        assertThrows(exception, () -> picture[0] = bean.put(base64));
        assertNull(picture[0]);
        assertEquals(0, container.getCollection("pictures").countDocuments(eq("base64", base64)));
    }

    /**
     * Tests the {@link PicturesBean#get(ObjectId)} function with existing PictureIds.
     * @throws IOException
     */
    @Test
    void getTest() throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient());

        Object[][] pictures = getDemoPictures();
        placePictures(pictures);

        for(Object[] picture : pictures) {
            Picture p = bean.get((ObjectId) picture[0]);
            assertNotNull(p);
            assertEquals(p.getID(), picture[0]);
            assertEquals(p.getBase64(), picture[1]);
            assertEquals(p.getContentType(), picture[2]);
        }
    }

    /**
     * Tests the {@link PicturesBean#get(ObjectId)} with non existing pictures.
     * @throws IOException
     */
    @Test
    void failGetTest() throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient());

        Object[][] pictures = getDemoPictures();
        placePictures(pictures);

        Picture[] picture = new Picture[1];
        assertThrows(PictureNotFoundException.class, () -> picture[0] = bean.get(new ObjectId("000000000000000000000006")));
        assertNull(picture[0]);
    }
}
