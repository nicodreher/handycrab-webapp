package de.dhbw.handycrab.server.test.beans.pictures;

import de.dhbw.handycrab.api.pictures.Picture;
import de.dhbw.handycrab.server.beans.pictures.PicturesBean;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
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

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class PicturesBeanTest {

    @Container
    private MongoContainer container = new MongoContainer();

    public void placePicture(ObjectId objectId, String base64, String contentType) {
        Document doc = new Document();
        doc.put("_id", objectId);
        doc.put("base64", base64);
        doc.put("contentType", contentType);
        container.getCollection("pictures").insertOne(doc);
    }

    public void placePictures(Object[][] data) {
        for(Object[] picture : data) {
            placePicture((ObjectId) picture[0], (String) picture[1], (String) picture[2]);
        }
    }

    public Object[][] getDemoPictures() throws IOException {
        return new Object[][]{{new ObjectId("000000000000000000000000"), getFileAsBase64("/pictures/images/success/jpeg1.jpg"), "image/jpeg"},
                {new ObjectId("000000000000000000000001"), getFileAsBase64("/pictures/images/success/jpeg2.jpeg"), "image/jpeg"},
                {new ObjectId("000000000000000000000002"), getFileAsBase64("/pictures/images/success/jpeg3.jpg"), "image/jpeg"},
                {new ObjectId("000000000000000000000003"), getFileAsBase64("/pictures/images/success/png1.png"), "image/png"},
                {new ObjectId("000000000000000000000004"), getFileAsBase64("/pictures/images/success/png2.png"), "image/png"},
                {new ObjectId("000000000000000000000005"), getFileAsBase64("/pictures/images/success/png3.png"), "image/png"}};
    }

    public String getFileAsBase64(String path) throws IOException {
        return Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream(path)));
    }

    @ParameterizedTest(name = "[{index}] Path: {0} Format: {1}")
    @CsvFileSource(resources = "/pictures/put.csv")
    public void putTest(String picturePath, String format) throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient(), new SerializerBean());

        String base64 = Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream(picturePath)));
        Picture picture = bean.put(base64);
        assertNotNull(picture);
        assertEquals(format, picture.getContentType());
        assertNotNull(picture.getID());
        Document doc = container.getCollection("pictures").find(eq("_id", picture.getID())).first();
        assertNotNull(doc);
        assertEquals(base64, doc.getString("base64"));
        assertEquals(format, doc.getString("contentType"));
    }

    @ParameterizedTest(name = "[{index}] Path: {0} ExpectedException {1}")
    @CsvFileSource(resources = "/pictures/failPut.csv")
    public void failPutTest(String picturePath, String expectedException) throws IOException, ClassNotFoundException {
        PicturesBean bean = new PicturesBean(container.getMongoClient(), new SerializerBean());

        String base64 = picturePath != null ? Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream(picturePath))) : null;
        Class<? extends Throwable> exception = (Class<? extends Throwable>) Class.forName(expectedException);

        final Picture[] picture = new Picture[1];
        assertThrows(exception, () -> picture[0] = bean.put(base64));
        assertNull(picture[0]);
        assertEquals(0, container.getCollection("pictures").countDocuments());
    }

    @Test
    public void getTest() throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient(), new SerializerBean());

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

    @Test
    public void failGetTest() throws IOException {
        PicturesBean bean = new PicturesBean(container.getMongoClient(), new SerializerBean());

        Object[][] pictures = getDemoPictures();
        placePictures(pictures);

        Picture[] picture = new Picture[1];
        assertThrows(PictureNotFoundException.class, () -> picture[0] = bean.get(new ObjectId("000000000000000000000006")));
        assertNull(picture[0]);
    }
}
