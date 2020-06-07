package de.dhbw.handycrab.server.beans.barriers;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import de.dhbw.handycrab.api.barriers.*;
import de.dhbw.handycrab.api.pictures.Pictures;
import de.dhbw.handycrab.api.utils.Serializer;
import de.dhbw.handycrab.exceptions.IncompleteRequestException;
import de.dhbw.handycrab.exceptions.barriers.BarrierNotFoundException;
import de.dhbw.handycrab.exceptions.barriers.InvalidGeoPositionException;
import de.dhbw.handycrab.exceptions.barriers.InvalidUserIdException;
import de.dhbw.handycrab.exceptions.barriers.SolutionNotFoundException;
import de.dhbw.handycrab.server.beans.persistence.DataSource;
import de.dhbw.handycrab.server.beans.persistence.RequestBuilder;
import de.dhbw.handycrab.server.beans.pictures.PicturesBean;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link Barriers} interface
 *
 * @author Lukas Lautenschlager
 * @see Barriers
 */
@Stateless
@Remote(Barriers.class)
public class BarriersBean implements Barriers {

    @Resource(lookup = "java:global/MongoClient")
    private MongoClient client;

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    @Resource(lookup = Pictures.LOOKUP)
    private Pictures picturesBean;

    private DataSource<Barrier> dataSource;

    public BarriersBean() {
    }

    public BarriersBean(MongoClient client, Serializer serializer) {
        this.client = client;
        this.serializer = serializer;
        construct();
    }

    public BarriersBean(MongoClient client, Serializer serializer, PicturesBean pictures) {
        this.client = client;
        this.serializer = serializer;
        this.picturesBean = pictures;
        construct();
    }

    @PostConstruct
    private void construct() {
        dataSource = new DataSource<>(Barrier.class, "barriers", serializer, client);
        dataSource.getCollection().createIndex(Indexes.geo2dsphere("point"));
    }

    @Override
    public Barrier getBarrier(ObjectId id) {
        if(id != null) {
            if(dataSource.contains(id)) {
                return dataSource.get(id);
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        throw new IncompleteRequestException();
    }

    @Override
    public List<Barrier> getBarrierOnUserId(ObjectId requesterId) {
        if(requesterId != null) {
            return dataSource.find(new RequestBuilder()
                    .filter(Filters.eq("userId", requesterId)))
                    .collect(Collectors.toList());
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public List<Barrier> getBarrier(String postcode) {
        if(postcode != null) {
            return dataSource.find(new RequestBuilder()
                    .filter(Filters.eq("postcode", postcode)))
                    .collect(Collectors.toList());
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public List<Barrier> getBarrier(double longitude, double latitude, int radius, boolean toDelete) {
        if(longitude <= 180 && longitude >= -180 && latitude <= 90 && latitude >= -90) {
            Point point = new Point(new Position(longitude, latitude));
            var barriersStream = dataSource
                    .find(new RequestBuilder().filter(Filters.nearSphere("point", point, (double) radius, 0d)));
            if(!toDelete) {
                return barriersStream.collect(Collectors.toList());
            }
            else {
                return barriersStream.filter(Barrier::isDelete).collect(Collectors.toList());
            }
        }
        else {
            throw new InvalidGeoPositionException();
        }
    }


    @Override
    public Barrier addBarrier(String title, double longitude, double latitude, String picture, String postalCode,
            String description, String solution, ObjectId userId) {
        if(title != null && postalCode != null && description != null) {
            if(longitude <= 180 && longitude >= -180 && latitude <= 90 && latitude >= -90) {
                var barrier = new BarrierBuilder().title(title).point(longitude, latitude).postalCode(postalCode)
                        .description(description).userId(userId);
                if(solution != null && userId != null) {
                    var solObject = new Solution();
                    solObject.setText(solution);
                    solObject.setUserId(userId);
                    barrier.solution(solObject);
                }
                if(picture != null) {
                    var pic = picturesBean.put(picture);
                    barrier.picture(pic.getID());
                }
                var bar = barrier.build();
                dataSource.insert(bar);
                return bar;
            }
            else {
                throw new InvalidGeoPositionException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public Barrier modifyBarrier(ObjectId id, String title, String picture, String description, ObjectId userId) {
        if(id != null) {
            Barrier barrier = dataSource.get(id);
            if(!barrier.getUserId().equals(userId)) {
                throw new InvalidUserIdException();
            }
            if(title != null) {
                barrier.setTitle(title);
            }
            if(description != null) {
                barrier.setDescription(description);
            }
            if(picture != null) {
                var pic = picturesBean.put(picture);
                barrier.setPicture(pic.getID());
            }
            dataSource.update(barrier);
            return barrier;
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public Barrier addVoteToBarrier(ObjectId id, Vote vote, ObjectId userId) {
        if(id != null && vote != null && userId != null) {
            if(dataSource.contains(id)) {
                Barrier barrier = dataSource.get(id);
                var upVotes = barrier.getUpVotes();
                var downVotes = barrier.getDownVotes();
                if(vote == Vote.UP && !upVotes.contains(userId)) {
                    downVotes.remove(userId);
                    upVotes.add(userId);
                }
                else if(vote == Vote.DOWN && !downVotes.contains(userId)) {
                    upVotes.remove(userId);
                    downVotes.add(userId);
                }
                else if(vote == Vote.NONE) {
                    upVotes.remove(userId);
                    downVotes.remove(userId);
                }
                dataSource.update(barrier);
                return barrier;
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public Barrier addSolution(ObjectId id, String solution, ObjectId userId) {
        if(id != null && solution != null && userId != null) {
            if(dataSource.contains(id)) {
                var barrier = dataSource.get(id);
                var solutionToAdd = new Solution();
                solutionToAdd.setText(solution);
                solutionToAdd.setUserId(userId);
                barrier.getSolutions().add(solutionToAdd);
                dataSource.update(barrier);
                return barrier;
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public Barrier addVoteToSolution(ObjectId solutionId, Vote vote, ObjectId userId) {
        if(solutionId != null && vote != null && userId != null) {
            Barrier barrier =
                    dataSource.findFirst(new RequestBuilder().filter(Filters.eq("solutions._id", solutionId)));
            if(barrier != null) {
                var solutionObj =
                        barrier.getSolutions().stream().filter(solution -> solution.getId().equals(solutionId))
                                .findFirst().orElseGet(null);
                if(vote == Vote.UP && !solutionObj.getUpVotes().contains(userId)) {
                    solutionObj.getDownVotes().remove(userId);
                    solutionObj.getUpVotes().add(userId);
                }
                else if(vote == Vote.DOWN && !solutionObj.getDownVotes().contains(userId)) {
                    solutionObj.getUpVotes().remove(userId);
                    solutionObj.getDownVotes().add(userId);
                }
                else if(vote == Vote.NONE) {
                    solutionObj.getDownVotes().remove(userId);
                    solutionObj.getUpVotes().remove(userId);
                }
            }
            else {
                throw new SolutionNotFoundException();
            }
            dataSource.update(barrier);
            return barrier;
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public boolean deleteBarrier(ObjectId id, ObjectId requesterId) {
        if(id != null && requesterId != null) {
            if(dataSource.contains(id)) {
                var barrier = dataSource.get(id);
                if(barrier.getUserId().equals(requesterId)) {
                    dataSource.deleteOne(id);
                    return true;
                }
                else {
                    throw new InvalidUserIdException();
                }
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public Barrier addCommentToBarrier(ObjectId barrierId, String comment, ObjectId requesterId) {
        if(barrierId != null && requesterId != null) {
            if(dataSource.contains(barrierId)) {
                var barrier = dataSource.get(barrierId);
                barrier.addComment(comment, requesterId);
                dataSource.update(barrier);
                return barrier;
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }

    @Override
    public boolean markBarrierForDeletion(ObjectId barrierId, ObjectId requesterId) {
        if(barrierId != null && requesterId != null) {
            if(dataSource.contains(barrierId)) {
                var barrier = dataSource.get(barrierId);
                barrier.setDelete(true);
                dataSource.update(barrier);
                return true;
            }
            else {
                throw new BarrierNotFoundException();
            }
        }
        else {
            throw new IncompleteRequestException();
        }
    }
}
