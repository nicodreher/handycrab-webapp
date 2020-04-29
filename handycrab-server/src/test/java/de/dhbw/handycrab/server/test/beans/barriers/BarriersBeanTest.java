package de.dhbw.handycrab.server.test.beans.barriers;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import de.dhbw.handycrab.api.barriers.FrontendBarrier;
import de.dhbw.handycrab.api.barriers.Solution;
import de.dhbw.handycrab.api.barriers.Vote;
import de.dhbw.handycrab.server.beans.barriers.BarriersBean;
import de.dhbw.handycrab.server.beans.utils.SerializerBean;
import de.dhbw.handycrab.server.exceptions.BarrierNotFoundException;
import de.dhbw.handycrab.server.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.server.exceptions.InvalidGeoPositionException;
import de.dhbw.handycrab.server.exceptions.InvalidUserIdException;
import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class BarriersBeanTest {

    private static final ObjectId REQUESTERID = new ObjectId("000000000000000000000000");
    @Container
    private MongoContainer container = new MongoContainer();

    private Document generateBarrier(ObjectId id, ObjectId userId, String title, double longitude, double latitude, String description, String postcode, List<Solution> solutions, List<Vote> votes) {
        var position = new Point(new Position(longitude, latitude));
        Document doc = new Document();
        doc.put("_id", id);
        doc.put("userId", userId);
        doc.put("title", title);
        doc.put("position", position);
        doc.put("description", description);
        doc.put("postcode", postcode);
        doc.put("solutions", solutions);
        doc.put("votes", votes);
        return doc;
    }

    private Document[] generateBarriers() {
        return new Document[]{
                generateBarrier(new ObjectId("000000000000000000000000"), new ObjectId("000000000000000000000000"), "Bordstein behindert mich", 60, 40, "Der Bordstein behindert mich", "XYZ123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000001"), new ObjectId("000000000000000000000001"), "Treppe nicht barrierefrei", 60, 40, "Treppe nicht barrierefrei", "ABC123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000002"), new ObjectId("000000000000000000000002"), "Keine Behindertenparkplätze", 60, 40, "Keine Behindertenparkplätze", "XYZ123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000003"), new ObjectId("000000000000000000000003"), "Vorsicht Pflasterstein", 60, 40, "Vorsicht Pflasterstein", "ABC123", new ArrayList<>(), new ArrayList<>()),
                generateBarrier(new ObjectId("000000000000000000000004"), new ObjectId("000000000000000000000004"), "ABC", 60, 40, "ABC", "ABC123", new ArrayList<>(), new ArrayList<>())
        };
    }

    @BeforeEach
    public void initializeBean() {
        insertBarriers();
    }

    public void insertBarriers() {
        var docs = generateBarriers();
        container.getCollection("barriers").insertMany(Arrays.stream(docs).collect(Collectors.toList()));
    }

    @Test
    void getBarrier_onPostCode_ReturnsBarriers() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());


        List<FrontendBarrier> bar = bean.getBarrier("XYZ123", REQUESTERID);

        assertEquals(2, bar.size());
        assertNotNull(bar.get(0));
        assertNotNull(bar.get(1));
        assertEquals(new ObjectId("000000000000000000000000"), bar.get(0).get_id());
        assertEquals(new ObjectId("000000000000000000000002"), bar.get(1).get_id());
    }

    @Test
    void getBarrier_onId_ReturnsBarrier() {
        var _id = new ObjectId("000000000000000000000000");
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var idAsJSON = new JSONObject().put("_id", _id).toString();

        FrontendBarrier bar = bean.getBarrier(_id, REQUESTERID);

        assertNotNull(bar);
        assertEquals(_id, bar.get_id());
    }

    @Test
    void getBarrier_onIdInvalidBarrier_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000012");

        assertThrows(BarrierNotFoundException.class, () -> bean.getBarrier(_id, REQUESTERID));
    }

    @Test
    void addBarrier_validBarrier_BarrierSavedInMongoDB() {
        var userId = new ObjectId("000000000000000000000000");
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var barrier = generateValidBarrierAsJSON();

        bean.addBarrier(
                barrier.optString("title", null),
                barrier.optDouble("longitude", 200d),
                barrier.optDouble("latitude", 200d),
                barrier.optString("postcode", null),
                barrier.optString("description", null),
                barrier.optString("solution", null),
                userId);

        List<FrontendBarrier> result = bean.getBarrier("70000", REQUESTERID);
        var resultBar = result.get(0);
        assertNotNull(result);
        assertEquals(userId, resultBar.getUserId());
        assertEquals("TestBarrier", resultBar.getTitle());
        assertEquals(40d, resultBar.getLongitude());
        assertEquals(30d, resultBar.getLatitude());
        assertEquals("70000", resultBar.getPostcode());
    }

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
                        barrier.optString("postcode", null),
                        barrier.optString("description", null),
                        barrier.optString("solution", null),
                        userId));

    }

    @Test
    void addBarrier_IncompleteBarrier_ThrowsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var userId = new ObjectId("000000000000000000000000");
        var barrier = generateBarrierWithIncompleteInfo();

        assertThrows(IncompleteRequestException.class, () -> bean.addBarrier(barrier.optString("title", null), barrier.optDouble("longitude", 200d), barrier.optDouble("latitude", 200d), barrier.optString("postcode", null), barrier.optString("description", null), barrier.optString("solution", null), userId));
    }

    private JSONObject generateBarrierWithIncompleteInfo() {
        var bar = new JSONObject();
        bar.put("latitude", 30d).put("longitude", 40d);
        return bar;
    }

    private JSONObject generateValidBarrierAsJSON() {
        var bar = new JSONObject();
        bar.put("title", "TestBarrier").put("latitude", 30d).put("longitude", 40d).put("postcode", "70000").put("description", "Beschreibung");
        return bar;
    }

    private JSONObject generateBarrierWithInvalidGeoPositionAsJSON() {
        var bar = new JSONObject();
        bar.put("title", "TestBarrier").put("latitude", 300d).put("longitude", 40d).put("postcode", "70000").put("description", "Beschreibung");
        return bar;
    }

    @Test
    void modifyBarrier_validBarrier_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var description = "Changed description and title";
        var title = "Changed title";

        bean.modifyBarrier(_id, title, description, REQUESTERID);

        FrontendBarrier result = bean.getBarrier(_id, REQUESTERID);
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
    }

    @Test
    void modifyBarrier_noId_throwsIncompleteRequestException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());

        assertThrows(IncompleteRequestException.class, () -> bean.modifyBarrier(null, "test", "test", REQUESTERID));
    }

    @Test
    void putVote_upVoteBarrier_voteSavedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        var vote = Vote.UP;

        bean.putVote(_id, vote, userId);

        var barrier = bean.getBarrier(_id, userId);
        assertNotNull(barrier);
        assertEquals(_id, barrier.get_id());
        assertEquals(1, barrier.getUpVotes());
    }

    @Test
    void addSolution_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");
        var solution = "Neue Lösung für Barriere";

        bean.addSolution(_id, solution, userId);

        FrontendBarrier result = bean.getBarrier(_id, REQUESTERID);
        assertNotNull(result);
        assertEquals(solution, result.getSolutions().get(0).getText());
    }

    @Test
    void addSolution_invalidUserId_throwsInvalidUserIdException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        ObjectId userId = null;
        var solution = "New solution for barrier";

        assertThrows(InvalidUserIdException.class, () -> bean.addSolution(_id, solution, userId));
    }

    @Test
    void addSolution_invalidBarrierId_throwsBarrierNotFoundException() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000012");
        var userId = new ObjectId("000000000000000000000000");
        var solution = "Neue Lösung für Barrier";

        assertThrows(BarrierNotFoundException.class, () -> bean.addSolution(_id, solution, userId));
    }

    @Test
    void addVoteToSolution_UPVote_savedInMongoDB() {
        var bean = new BarriersBean(container.getMongoClient(), new SerializerBean());
        var _id = new ObjectId("000000000000000000000000");
        var userId = new ObjectId("000000000000000000000000");

        bean.addSolution(_id, "Neue Lösung", userId);
        var solutionId = bean.getBarrier(_id, userId).getSolutions().get(0).get_id();
        bean.addVoteToSolution(solutionId, Vote.UP, userId);

        assertEquals(1, bean.getBarrier(_id, userId).getSolutions().get(0).getUpVotes());
        assertEquals(0, bean.getBarrier(_id, userId).getSolutions().get(0).getDownVotes());
    }
}