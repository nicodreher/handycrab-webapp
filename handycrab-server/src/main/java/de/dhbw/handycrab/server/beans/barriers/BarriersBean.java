package de.dhbw.handycrab.server.beans.barriers;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import de.dhbw.handycrab.api.barriers.*;
import de.dhbw.handycrab.api.users.Users;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.server.beans.persistence.DataSource;
import de.dhbw.handycrab.server.beans.persistence.RequestBuilder;
import de.dhbw.handycrab.server.exceptions.*;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Remote(Barriers.class)
public class BarriersBean implements Barriers {

    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    @Resource(lookup = Users.LOOKUP)
    private Users userBean;

    private DataSource<Barrier> dataSource;

    private DataSource<Solution> solutionDataSource;

    public BarriersBean() {
    }

    public BarriersBean(MongoClient client, Serializer serializer) {
        this.client = client;
        this.serializer = serializer;
        construct();
    }

    @PostConstruct
    private void construct() {
        dataSource = new DataSource<>(Barrier.class, "barriers", serializer, client);
    }

    @Override
    public FrontendBarrier getBarrier(ObjectId id, ObjectId userId) {
        if (id != null) {
            if (dataSource.contains(id)) {
                return new FrontendBarrier(dataSource.get(id), userId);
            } else
                throw new BarrierNotFoundException();
        }
        throw new IncompleteRequestException();
    }

    @Override
    public List<FrontendBarrier> getBarrier(String postcode, ObjectId userId) {
        if (postcode != null) {
            return dataSource.find(new RequestBuilder()
                    .filter(Filters.eq("postcode", postcode)))
                    .map(bar -> new FrontendBarrier(bar, userId))
                    .collect(Collectors.toList());
        } else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public List<FrontendBarrier> getBarrier(double longitude, double latitude, int radius, ObjectId userId) {
        if (latitude <= 180 && latitude >= -180 && longitude <= 90 && longitude >= -90) {
            Point point = new Point(new Position(longitude, latitude));
            return dataSource.find(new RequestBuilder().filter(Filters.nearSphere("point", point, (double) radius, 0d)))
                    .map(e -> new FrontendBarrier(e, userId)).collect(Collectors.toList());
        } else throw new InvalidGeoPositionException();
    }

    @Override
    public FrontendBarrier addBarrier(String title, double longitude, double latitude, String postalCode, String description, String solution, ObjectId userId) {
        if (title != null && postalCode != null && description != null) {
            if (longitude <= 90 && latitude <= 180) {
                var barrier = new BarrierBuilder().title(title).point(longitude, latitude).postalCode(postalCode).description(description).userId(userId);
                if (solution != null && userId != null) {
                    var solObject = new Solution();
                    solObject.setText(solution);
                    solObject.setUserId(userId);
                    barrier.solution(solObject);
                }
                var bar = barrier.build();
                dataSource.insert(bar);
                return new FrontendBarrier(bar, userId);
            } else
                throw new InvalidGeoPositionException();
        } else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public FrontendBarrier modifyBarrier(ObjectId id, String title, String description, ObjectId userId) {
        if (id != null) {
            Barrier barrier = dataSource.get(id);
            if (title != null)
                barrier.setTitle(title);
            if (description != null)
                barrier.setDescription(description);
            dataSource.update(barrier);
            return new FrontendBarrier(barrier, userId);
        } else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public FrontendBarrier putVote(ObjectId id, Vote vote, ObjectId userId) {
        if (dataSource.contains(id)) {
            Barrier barrier = dataSource.get(id);
            var upVotes = barrier.getUpVotes();
            var downVotes = barrier.getDownVotes();
            if (vote == Vote.UP && !upVotes.contains(userId)) {
                downVotes.remove(userId);
                upVotes.add(userId);
            } else if (vote == Vote.DOWN && downVotes.contains(userId)) {
                upVotes.remove(userId);
                downVotes.add(userId);
            } else if (vote == Vote.NONE) {
                upVotes.remove(userId);
                downVotes.remove(userId);
            }
            dataSource.update(barrier);
            return new FrontendBarrier(barrier, userId);
        } else
            throw new BarrierNotFoundException();
    }

    @Override
    public FrontendBarrier addSolution(ObjectId id, String solution, ObjectId userId) {
        if (dataSource.contains(id)) {
            if (userId != null) {
                var barrier = dataSource.get(id);
                var solutionToAdd = new Solution();
                solutionToAdd.setText(solution);
                solutionToAdd.setUserId(userId);
                barrier.getSolutions().add(solutionToAdd);
                dataSource.update(barrier);
                return new FrontendBarrier(barrier, userId);
            } else
                throw new InvalidUserIdException();
        } else
            throw new BarrierNotFoundException();
    }

    @Override
    public FrontendBarrier addVoteToSolution(ObjectId solutionId, Vote vote, ObjectId userId) {
        Barrier barrier = dataSource.findFirst(new RequestBuilder().filter(Filters.eq("solutions._id", solutionId)));
        if (barrier != null) {
            var solutionObj = barrier.getSolutions().stream().filter(solution -> solution.getId().equals(solutionId)).findFirst().orElseGet(null);
            if (vote == Vote.UP && !solutionObj.getUpVotes().contains(userId)) {
                solutionObj.getDownVotes().remove(userId);
                solutionObj.getUpVotes().add(userId);
            } else if (vote == Vote.DOWN && !solutionObj.getDownVotes().contains(userId)) {
                solutionObj.getUpVotes().remove(userId);
                solutionObj.getDownVotes().add(userId);
            } else if (vote == Vote.NONE) {
                solutionObj.getDownVotes().remove(userId);
                solutionObj.getUpVotes().remove(userId);
            }
        } else {
            throw new SolutionNotFoundException();
        }
        dataSource.update(barrier);
        return new FrontendBarrier(barrier, userId);
    }
}
