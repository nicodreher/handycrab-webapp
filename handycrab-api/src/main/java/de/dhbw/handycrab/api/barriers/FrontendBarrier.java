package de.dhbw.handycrab.api.barriers;

import de.dhbw.handycrab.api.pictures.Pictures;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for representing a Barrier, which is used by the REST-Clients.
 * Up- and Downvotes only represented by integers (amount of up- and downvotes).
 *
 * @author Lukas Lautenschlager
 */
public class FrontendBarrier implements Serializable {
    private ObjectId _id;
    private ObjectId userId;
    private String title;
    /**
     * URL to retrieve the picture from the REST-Service.
     */
    private String picturePath;
    private double longitude;
    private double latitude;
    private String description;
    private String postcode;
    private List<FrontendSolution> solutions;
    private List<Comment> comments;
    private int upVotes;
    private int downVotes;
    /**
     * Represent the value for a vote of the requested user.
     */
    private Vote vote;

    public FrontendBarrier() {
    }

    public FrontendBarrier(Barrier barrier, ObjectId userId) {
        this._id = barrier.get_id();
        this.userId = barrier.getUserId();
        this.title = barrier.getTitle();
        if(barrier.getPicture() != null) {
            this.picturePath = Pictures.BASE_URL + "/" + barrier.getPicture().toString();
        }
        else {
            this.picturePath = null;
        }
        this.longitude = barrier.getLongitude();
        this.latitude = barrier.getLatitude();
        this.description = barrier.getDescription();
        this.postcode = barrier.getPostcode();
        this.solutions =
                barrier.getSolutions().stream().map(e -> new FrontendSolution(e, userId)).collect(Collectors.toList());
        this.upVotes = barrier.getUpVotes().size();
        this.downVotes = barrier.getDownVotes().size();
        this.comments = barrier.getComments();
        if(barrier.getUpVotes().contains(userId)) {
            this.vote = Vote.UP;
        }
        else if(barrier.getDownVotes().contains(userId)) {
            this.vote = Vote.DOWN;
        }
        else {
            this.vote = Vote.NONE;
        }
    }

    public ObjectId get_id() {
        return _id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public List<FrontendSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<FrontendSolution> solutions) {
        this.solutions = solutions;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
