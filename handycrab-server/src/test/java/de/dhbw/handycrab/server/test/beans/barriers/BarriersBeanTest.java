package de.dhbw.handycrab.server.test.beans.barriers;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import de.dhbw.handycrab.api.barriers.Barrier;
import de.dhbw.handycrab.api.barriers.FrontendBarrier;
import de.dhbw.handycrab.api.barriers.Solution;
import de.dhbw.handycrab.api.barriers.Vote;
import de.dhbw.handycrab.api.pictures.Pictures;
import de.dhbw.handycrab.exceptions.*;
import de.dhbw.handycrab.exceptions.barriers.BarrierNotFoundException;
import de.dhbw.handycrab.exceptions.barriers.InvalidGeoPositionException;
import de.dhbw.handycrab.exceptions.barriers.InvalidUserIdException;
import de.dhbw.handycrab.exceptions.barriers.SolutionNotFoundException;
import de.dhbw.handycrab.server.beans.barriers.BarriersBean;
import de.dhbw.handycrab.server.beans.pictures.PicturesBean;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import org.apache.commons.compress.utils.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link BarriersBean} class
 *
 * @author Lukas Lautenschlager
 */
@Testcontainers
class BarriersBeanTest {

    private static final ObjectId REQUESTERID = new ObjectId("000000000000000000000000");
    @Container
    private MongoContainer container = new MongoContainer();

    /**
     * Generates a Barrier as bson document
     *
     * @param id
     * @param userId
     * @param title
     * @param longitude
     * @param latitude
     * @param description
     * @param postcode
     * @param solutions
     * @param votes
     * @return
     */
    private Document generateBarrier(ObjectId id, ObjectId userId, String title, double longitude, double latitude, String description, String postcode, List<Solution> solutions, List<Vote> votes) {
        var point = new Point(new Position(longitude, latitude));
        Document doc = new Document();
        doc.put("_id", id);
        doc.put("userId", userId);
        doc.put("title", title);
        doc.put("point", point);
        doc.put("description", description);
        doc.put("postcode", postcode);
        doc.put("solutions", solutions);
        doc.put("votes", votes);
        return doc;
    }

    /**
     * Generates multiple barriers
     *
     * @return array of documents
     */
    private Document[] generateBarriers() {
        return new Document[]{
                generateBarrier(new ObjectId("000000000000000000000000"), new ObjectId("000000000000000000000000"), "Bordstein behindert mich", 60, 40, "Der Bordstein behindert mich", "XYZ123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000001"), new ObjectId("000000000000000000000001"), "Treppe nicht barrierefrei", 60, 40, "Treppe nicht barrierefrei", "ABC123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000002"), new ObjectId("000000000000000000000002"), "Keine Behindertenparkplätze", 30, 50, "Keine Behindertenparkplätze", "XYZ123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000003"), new ObjectId("000000000000000000000003"), "Vorsicht Pflasterstein", 61, 41, "Vorsicht Pflasterstein", "ABC123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000004"), new ObjectId("000000000000000000000004"), "ABC", 60.00001, 40.00001, "ABC", "ABC123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000005"), new ObjectId("000000000000000000000000"), "Ich bin #2", 30, 40, "Beschreibung", "ABC1234", new ArrayList<>(), new ArrayList<>())
        };
    }


    /**
     * Initializes a bean before each unit test
     */
    @BeforeEach
    public void initializeBean() {
        insertBarriers();
    }

    /**
     * Insert the generated documents into a MongoDB
     */
    public void insertBarriers() {
        var docs = generateBarriers();
        container.getCollection("barriers").insertMany(Arrays.stream(docs).collect(Collectors.toList()));
    }

    /**
     * Test the {@link BarriersBean#getBarrier(ObjectId)} with valid inputs.
     * Returns list of barriers with userId = requesterId
     */
    @Test
    void getBarrier_onUserId_ReturnsBarrier() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        var result = bean.getBarrierOnUserId(REQUESTERID);

        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNotNull(result.get(1));
        assertEquals(new ObjectId("000000000000000000000005"), result.get(1).get_id());
        assertEquals(new ObjectId("000000000000000000000000"), result.get(0).get_id());
    }

    /**
     * Test the {@link BarriersBean#getBarrier(ObjectId)} with null UserId.
     * Throws IncompleteRequestException
     */
    @Test
    void getBarrier_onUserIdWithNull_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.getBarrierOnUserId(null));
    }

    /**
     * Test the {@link BarriersBean#getBarrier(String) with valid inputs},
     * dependent on a postal code and a userId
     */
    @Test
    void getBarrier_onPostCode_ReturnsBarriers() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());


        List<Barrier> bar = bean.getBarrier("XYZ123");

        assertEquals(2, bar.size());
        assertNotNull(bar.get(0));
        assertNotNull(bar.get(1));
        assertEquals(new ObjectId("000000000000000000000000"), bar.get(0).get_id());
        assertEquals(new ObjectId("000000000000000000000002"), bar.get(1).get_id());
    }

    /**
     * Test for the {@link BarriersBean#getBarrier(String)} with incomplete request information.
     */
    @Test
    void getBarrier_onPostCodeWithInvalidRequestInformation_throwsIncompleteRequestException() {
        String postcode = null;
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.getBarrier(postcode));
    }

    /**
     * Test for the {@link BarriersBean#getBarrier(double, double, int)} with valid inputs.
     * Dependent on geological position and radius.
     */
    @Test
    void getBarrier_onPosition_returnsBarriers() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        double longitude = 60;
        double latitude = 40;
        int radius = 10;

        var result = bean.getBarrier(longitude, latitude, radius);

        assertNotNull(result);
        assertEquals(3, result.size());
        var resultIds = result.stream().map(e -> e.get_id().toString()).collect(Collectors.toList());
        assertTrue(resultIds.contains("000000000000000000000000"));
        assertTrue(resultIds.contains("000000000000000000000001"));
        assertTrue(resultIds.contains("000000000000000000000004"));
    }

    /**
     * Test for the {@link BarriersBean#getBarrier(double, double, int)} with invalid longitude
     */
    @Test
    void getBarrier_onPositionWithInvalidGeoPosition_throwsInvalidGeoPositionException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        double longitude = 300;
        double latitude = 40;
        int radius = 10;

        assertThrows(InvalidGeoPositionException.class, () -> bean.getBarrier(longitude, latitude, radius));
    }

    /**
     * Test for the {@link BarriersBean#getBarrier(double, double, int)} with invalid latitude
     */
    @Test
    void getBarrier_OnPositionInvalidLatitude_throwsInvalidGeoPositionException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        double longitude = 90;
        double latitude = 120;
        int radius = 10;

        assertThrows(InvalidGeoPositionException.class, () -> bean.getBarrier(longitude, latitude, radius));
    }

    /**
     * Test the {@link BarriersBean#getBarrier(ObjectId) with valid inputs},
     * dependent on an identifier and a userId
     */
    @Test
    void getBarrier_onId_ReturnsBarrier() {
        var _id = new ObjectId("000000000000000000000000");
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        var bar = bean.getBarrier(_id);

        assertNotNull(bar);
        assertEquals(_id, bar.get_id());
    }

    /**
     * Test for the {@link BarriersBean#getBarrier(ObjectId)} with incomplete request info.
     */
    @Test
    void getBarrier_onId_throwsIncompleteRequestException() {
        ObjectId _id = null;
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.getBarrier(_id));
    }

    /**
     * Test the {@link BarriersBean#getBarrier(String) with invalid inputs},
     * with an unknown Identifier.
     */
    @Test
    void getBarrier_onIdInvalidBarrier_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000012");

        assertThrows(BarrierNotFoundException.class, () -> bean.getBarrier(_id));
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with valid inputs
     */
    @Test
    void addBarrier_validBarrier_BarrierSavedInMongoDB() throws IOException {
        var userId = new ObjectId("000000000000000000000000");
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var barrier = generateValidBarrierAsJSON();

        bean.addBarrier(
                barrier.optString("title", null),
                barrier.optDouble("longitude", 200d),
                barrier.optDouble("latitude", 200d),
                barrier.optString("picture", null),
                barrier.optString("postcode", null),
                barrier.optString("description", null),
                barrier.optString("solution", null),
                userId);

        List<Barrier> result = bean.getBarrier("70000");
        var resultBar = result.get(0);
        assertNotNull(result);
        assertEquals(userId, resultBar.getUserId());
        assertEquals("TestBarrier", resultBar.getTitle());
        assertEquals(40d, resultBar.getLongitude());
        assertEquals(30d, resultBar.getLatitude());
        assertEquals("70000", resultBar.getPostcode());
        assertEquals(barrier.getString("picture"), picturesBean
                .get(resultBar.getPicture())
                .getBase64());
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with valid Parameters (edge case longitude = 180 and latitude = 90)
     */
    @Test
    void addBarrier_validEdgeCase_savedInMongoDB()
    {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var title = "Neue Barriere";
        double longitude = 180;
        double latitude = 90;
        var postalCode = "ABC123";
        var description = "Beschreibung";
        var solution = "Lösung für Barriere";

        var bar = bean.addBarrier(title,longitude,latitude,null, postalCode, description, solution, REQUESTERID);

        var result = bean.getBarrier(bar.get_id());
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(longitude, result.getLongitude());
        assertEquals(latitude, result.getLatitude());
        assertEquals(postalCode, result.getPostcode());
        assertEquals(description, result.getDescription());
        assertEquals(solution, result.getSolutions().get(0).getText());
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with valid Parameters (edge case longitude = -180 and latitude = -90)
     */
    @Test
    void addBarrier_validEdgeCaseNeg_savedInMongoDB()
    {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var title = "Neue Barriere";
        double longitude = -180;
        double latitude = -90;
        var postalCode = "ABC123";
        var description = "Beschreibung";
        var solution = "Lösung für Barriere";

        var bar = bean.addBarrier(title,longitude,latitude,null, postalCode, description, solution, REQUESTERID);

        var result = bean.getBarrier(bar.get_id());
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(longitude, result.getLongitude());
        assertEquals(latitude, result.getLatitude());
        assertEquals(postalCode, result.getPostcode());
        assertEquals(description, result.getDescription());
        assertEquals(solution, result.getSolutions().get(0).getText());
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with valid Parameters (edge case longitude = 180.1 and latitude = 90.1)
     */
    @Test
    void addBarrier_invalidEdgeCase_savedInMongoDB()
    {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var title = "Neue Barriere";
        double longitude = 180.1;
        double latitude = 90.1;
        var postalCode = "ABC123";
        var description = "Beschreibung";
        var solution = "Lösung für Barriere";

        assertThrows(InvalidGeoPositionException.class, () -> bean.addBarrier(title,longitude,latitude,null, postalCode, description, solution, REQUESTERID));
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with valid Parameters (edge case longitude = -180.1 and latitude = -90.1)
     */
    @Test
    void addBarrier_invalidEdgeCaseNeg_savedInMongoDB()
    {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var title = "Neue Barriere";
        double longitude = -180.1;
        double latitude = -90.1;
        var postalCode = "ABC123";
        var description = "Beschreibung";
        var solution = "Lösung für Barriere";

        assertThrows(InvalidGeoPositionException.class, () -> bean.addBarrier(title,longitude,latitude,null, postalCode, description, solution, REQUESTERID));
    }

    /**
     * Generates a valid JSON Object for processing
     *
     * @return request as JSON Object
     */
    private JSONObject generateValidBarrierAsJSON() throws IOException {
        var bar = new JSONObject();
        var pic = Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream("/pictures/images/success/jpeg1.jpg")));
        bar.put("title", "TestBarrier").put("latitude", 30d).put("longitude", 40d).put("postcode", "70000").put("description", "Beschreibung")
                .put("solution", "Eine von Vielen").put("picture", pic);
        return bar;
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with invalid GeoPositions
     */
    @Test
    void addBarrier_BarrierWithInvalidGeoPos_ThrowsInvalidGeoException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var barrier = generateBarrierWithInvalidGeoPositionAsJSON();

        assertThrows(InvalidGeoPositionException.class,
                () -> bean.addBarrier(
                        barrier.optString("title", null),
                        barrier.optDouble("longitude", 200d),
                        barrier.optDouble("latitude", 200d),
                        barrier.optString("picture", null),
                        barrier.optString("postcode", null),
                        barrier.optString("description", null),
                        barrier.optString("solution", null),
                        userId));

    }


    /**
     * Generates a request with invalid GeoPosition
     *
     * @return request with invalid GeoPosition
     */
    private JSONObject generateBarrierWithInvalidGeoPositionAsJSON() {
        var bar = new JSONObject();
        bar.put("title", "TestBarrier").put("latitude", 300d).put("longitude", 40d).put("postcode", "70000").put("description", "Beschreibung");
        return bar;
    }

    /**
     * Test the {@link BarriersBean#addBarrier(String, double, double, String, String, String, String, ObjectId)}
     * with incomplete request data
     */
    @Test
    void addBarrier_IncompleteBarrier_ThrowsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var barrier = generateBarrierWithIncompleteInfo();

        assertThrows(IncompleteRequestException.class, () -> bean.addBarrier(barrier.optString("title", null), barrier.optDouble("longitude", 200d), barrier.optDouble("latitude", 200d), barrier.optString("picture", null), barrier.optString("postcode", null), barrier.optString("description", null), barrier.optString("solution", null), userId));
    }

    /**
     * generates a JSON Object with incomplete information for processing
     *
     * @return request as JSON Object
     */
    private JSONObject generateBarrierWithIncompleteInfo() {
        var bar = new JSONObject();
        bar.put("latitude", 30d).put("longitude", 40d);
        return bar;
    }

    /**
     * Test for the {@link BarriersBean#modifyBarrier(ObjectId, String, String, String, ObjectId)}
     * with valid inputs
     */
    @Test
    void modifyBarrier_validBarrier_savedInMongoDB() throws IOException {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var _id = new ObjectId("000000000000000000000000");
        var title = "Changed title";
        var description = "Changed description and title";
        var pic = Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream("/pictures/images/success/jpeg1.jpg")));

        bean.modifyBarrier(_id, title, pic, description, REQUESTERID);

        var result = bean.getBarrier(_id);
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(pic, picturesBean
                .get(result.getPicture())
                .getBase64());
        assertEquals(description, result.getDescription());
    }

    /**
     * Test for the {@link BarriersBean#modifyBarrier(ObjectId, String, String, String, ObjectId)}
     * with no identifier of the barrier
     */
    @Test
    void modifyBarrier_noId_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.modifyBarrier(null, "test", "", "test", REQUESTERID));
    }

    /**
     * Test for the {@link BarriersBean#modifyBarrier(ObjectId, String, String, String, ObjectId)}
     * with userId != userId of the creator
     */
    @Test
    void modifyBarrier_invalidUserId_throwsInvalidUserIdException() throws IOException {
        var picturesBean = new PicturesBean(container.getMongoClient(), new SerializerBean());
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean(), picturesBean);
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000012");
        var title = "Neuer Titel";
        var description = "Neue Beschreibung";
        var pic = Base64.getEncoder().encodeToString(IOUtils.toByteArray(getClass().getResourceAsStream("/pictures/images/success/jpeg1.jpg")));

        assertThrows(InvalidUserIdException.class, () -> bean.modifyBarrier(_id, title, pic, description, userId));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with valid inputs
     */
    @Test
    void addVoteToBarrier_upVoteBarrier_voteSavedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        var vote = Vote.UP;

        bean.addVoteToBarrier(_id, vote, userId);

        var barrier = bean.getBarrier(_id);
        assertNotNull(barrier);
        assertEquals(_id, barrier.get_id());
        assertEquals(1, barrier.getUpVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addSolution(ObjectId, String, ObjectId)} with valid inputs
     */
    @Test
    void addSolution_validInputs_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        var solution = "Neue Lösung für Barriere";

        bean.addSolution(_id, solution, userId);

        var result = bean.getBarrier(_id);
        assertNotNull(result);
        assertEquals(solution, result.getSolutions().get(0).getText());
    }

    /**
     * Test for the {@link BarriersBean#addSolution(ObjectId, String, ObjectId)}
     * with invalid userId
     */
    @Test
    void addSolution_invalidUserId_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        ObjectId userId = null;
        var solution = "New solution for barrier";

        assertThrows(IncompleteRequestException.class, () -> bean.addSolution(_id, solution, userId));
    }

    /**
     * Test for the {@link BarriersBean#addSolution(ObjectId, String, ObjectId)}
     * with invalid identifier of the barrier
     */
    @Test
    void addSolution_invalidBarrierId_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000012");
        var userId = new ObjectId("000000000000000000000000");
        var solution = "Neue Lösung für Barrier";

        assertThrows(BarrierNotFoundException.class, () -> bean.addSolution(_id, solution, userId));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)}
     * with valid inputs (Upvote)
     */
    @Test
    void addVoteToSolution_UPVote_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");

        bean.addSolution(_id, "Neue Lösung", userId);
        var solutionId = bean.getBarrier(_id).getSolutions().get(0).getId();
        bean.addVoteToSolution(solutionId, Vote.UP, userId);

        var solution = bean.getBarrier(_id).getSolutions().get(0);
        assertEquals(1, solution.getUpVotes().size());
        assertEquals(0, solution.getDownVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)}
     * with valid inputs (Downvote)
     */
    @Test
    void addVoteToSolution_DOWNVote_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        bean.addSolution(_id, "Neue Lösung", userId);
        var solutionId = bean.getBarrier(_id).getSolutions().get(0).getId();

        bean.addVoteToSolution(solutionId, Vote.DOWN, userId);

        var solution = bean.getBarrier(_id).getSolutions().get(0);
        assertEquals(0, solution.getUpVotes().size());
        assertEquals(1, solution.getDownVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)}
     * with valid inputs with NONE Vote, that removes existing votes
     */
    @Test
    void addVoteSolutions_NONEVote_RemovesAllVotes() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        bean.addSolution(_id, "Neue Lösung", userId);
        var solutionId = bean.getBarrier(_id).getSolutions().get(0).getId();
        bean.addVoteToSolution(solutionId, Vote.DOWN, userId);

        bean.addVoteToSolution(solutionId, Vote.NONE, userId);

        var solution = bean.getBarrier(_id).getSolutions().get(0);
        assertNotNull(solution);
        assertEquals(0, solution.getDownVotes().size());
        assertEquals(0, solution.getUpVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)} with multiple votes from different users,
     * correct amount of Up- and Downvotes saved and vote of user is correct.
     */
    @Test
    void addVoteToSolution_differentUsersAndVotes_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000001");
        var userId2 = new ObjectId("000000000000000000000002");
        var userId3 = new ObjectId("000000000000000000000003");
        bean.addSolution(_id, "new Solution", userId);
        var solutionId = bean.getBarrier(_id).getSolutions().get(0).getId();

        bean.addVoteToSolution(solutionId, Vote.UP, userId);
        bean.addVoteToSolution(solutionId, Vote.DOWN, userId2);
        bean.addVoteToSolution(solutionId, Vote.DOWN, userId3);

        var solution = bean.getBarrier(_id).getSolutions().get(0);
        assertNotNull(solution);
        assertEquals(1, solution.getUpVotes().size());
        assertEquals(userId, solution.getUpVotes().get(0));
        assertEquals(2, solution.getDownVotes().size());
        assertTrue(solution.getDownVotes().contains(userId2));
        assertTrue(solution.getDownVotes().contains(userId3));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)}
     * with valid inputs. Upvote is removed by Downvote.
     */
    @Test
    void addVoteToSolution_UPtoDOWN_onlyDownSavedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        bean.addSolution(_id, "New Solution", userId);
        var solutionId = bean.getBarrier(_id).getSolutions().get(0).getId();
        bean.addVoteToSolution(solutionId, Vote.UP, userId);

        bean.addVoteToSolution(solutionId, Vote.DOWN, userId);

        var solution = bean.getBarrier(_id).getSolutions().get(0);
        assertNotNull(solution);
        assertEquals(1, solution.getDownVotes().size());
        assertTrue(solution.getDownVotes().contains(userId));
        assertEquals(0, solution.getUpVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)} with invalid solutionId
     */
    @Test
    void addVoteToSolution_invalidSolutionId_throwsSolutionNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var solutionId = new ObjectId("000000000000000000000000");

        assertThrows(SolutionNotFoundException.class, () -> bean.addVoteToSolution(solutionId, Vote.UP, userId));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToSolution(ObjectId, Vote, ObjectId)} with incomplete request information.
     * Throws IncompleteRequestException
     */
    @Test
    void addVoteToSolution_incompleteRequest_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.addVoteToSolution(null, null, REQUESTERID));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)}
     * with valid inputs (Upvote)
     */
    @Test
    void addVoteToBarrier_UPVote_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");

        bean.addVoteToBarrier(_id, Vote.UP, userId);

        var barrier = bean.getBarrier(_id);
        assertEquals(1, barrier.getUpVotes().size());
        assertTrue(barrier.getUpVotes().contains(userId));
        assertEquals(0, barrier.getDownVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with valid inputs (Downvote)
     */
    @Test
    void addVoteToBarrier_DOWNVote_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");

        bean.addVoteToBarrier(_id, Vote.DOWN, userId);

        var barrier = bean.getBarrier(_id);
        assertEquals(1, barrier.getDownVotes().size());
        assertTrue(barrier.getDownVotes().contains(userId));
        assertEquals(0, barrier.getUpVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with valid inputs.
     * NONE Vote removes existing votes of user.
     */
    @Test
    void addVoteToBarrier_NONEVote_removesCurrentVotes() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        bean.addVoteToBarrier(_id, Vote.UP, userId);

        bean.addVoteToBarrier(_id, Vote.NONE, userId);

        var barrier = bean.getBarrier(_id);
        assertEquals(0, barrier.getUpVotes().size());
        assertEquals(0, barrier.getDownVotes().size());
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with votes from multiple users.
     */
    @Test
    void addVoteToBarrier_MultipleVotes_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000001");
        var userId2 = new ObjectId("000000000000000000000002");
        var userId3 = new ObjectId("000000000000000000000003");
        var userId4 = new ObjectId("000000000000000000000004");

        bean.addVoteToBarrier(_id, Vote.UP, userId);
        bean.addVoteToBarrier(_id, Vote.DOWN, userId2);
        bean.addVoteToBarrier(_id, Vote.NONE, userId3);
        bean.addVoteToBarrier(_id, Vote.UP, userId4);

        var barrier = bean.getBarrier(_id);
        assertEquals(2, barrier.getUpVotes().size());
        assertTrue(barrier.getUpVotes().contains(userId));
        assertTrue(barrier.getUpVotes().contains(userId4));
        assertEquals(1, barrier.getDownVotes().size());
        assertTrue(barrier.getDownVotes().contains(userId2));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with invalid identifier for the barrier
     */
    @Test
    void addVoteToBarrier_invalidBarrierId_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var _id = new ObjectId("000000320000000000000000");

        assertThrows(BarrierNotFoundException.class, () -> bean.addVoteToBarrier(_id, Vote.UP, userId));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with valid inputs.
     * Upvote removes Downvote.
     */
    @Test
    void addVoteToBarrier_UPtoDOWN_onlyDOWNsavedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var _id = new ObjectId("000000000000000000000000");
        bean.addVoteToBarrier(_id, Vote.UP, userId);

        bean.addVoteToBarrier(_id, Vote.DOWN, userId);

        var barrier = bean.getBarrier(_id);
        assertEquals(0, barrier.getUpVotes().size());
        assertEquals(1, barrier.getDownVotes().size());
        assertTrue(barrier.getDownVotes().contains(userId));
    }

    /**
     * Test for the {@link BarriersBean#addVoteToBarrier(ObjectId, Vote, ObjectId)} with incomplete request information.
     * Throws IncompleteRequestException.
     */
    @Test
    void addVoteToBarrier_incompleteRequest_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.addVoteToSolution(null, null, REQUESTERID));
    }

    /**
     * Test for the {@link BarriersBean#deleteBarrier(ObjectId, ObjectId)} with valid Input.
     * Returns true
     */
    @Test
    void deleteBarrier_validInputs_DeletedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");

        var result = bean.deleteBarrier(_id, userId);

        assertNotNull(result);
        assertTrue(result);
        assertThrows(BarrierNotFoundException.class, () -> bean.getBarrier(_id));
    }

    /**
     * Test for the {@link BarriersBean#deleteBarrier(ObjectId, ObjectId)} with invalid identifier for barrier.
     * Throws BarrierNotFoundException
     */
    @Test
    void deleteBarrier_invalidIdForBarrier_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000027");

        assertThrows(BarrierNotFoundException.class, () -> bean.deleteBarrier(_id, REQUESTERID));
    }

    /**
     * Test for the {@link BarriersBean#deleteBarrier(ObjectId, ObjectId)} with invalid userId.
     * Throws InvalidUserIdException.
     */
    @Test
    void deleteBarrier_invalidUserId_throwsInvalidUserIdException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000027");

        assertThrows(InvalidUserIdException.class, () -> bean.deleteBarrier(_id, userId));
    }

    /**
     * Test for the {@link BarriersBean#deleteBarrier(ObjectId, ObjectId)} with incomplete Request information.
     * Throws Incomplete RequestException
     */
    @Test
    void deleteBarrier_incompleteRequest_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.deleteBarrier(null, REQUESTERID));
    }
}