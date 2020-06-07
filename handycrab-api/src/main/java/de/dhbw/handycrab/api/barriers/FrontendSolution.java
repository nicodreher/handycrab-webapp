package de.dhbw.handycrab.api.barriers;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Data structure of a solution for the REST-Clients.
 * Up- and Downvotes representing the amount.
 *
 * @author Lukas Lautenschlager
 */
public class FrontendSolution implements Serializable {
    private ObjectId _id;
    private ObjectId userId;
    private String text;
    private int upVotes;
    private int downVotes;
    /**
     * Represent the value for a vote of the requested user.
     */
    private Vote vote;

    public FrontendSolution() {
    }

    public FrontendSolution(Solution solution, ObjectId userIdForVote) {
        this._id = solution.getId();
        this.userId = solution.getUserId();
        this.text = solution.getText();
        this.upVotes = solution.getUpVotes().size();
        this.downVotes = solution.getDownVotes().size();
        if(solution.getUpVotes().contains(userIdForVote)) {
            this.vote = Vote.UP;
        }
        else if(solution.getDownVotes().contains(userIdForVote)) {
            this.vote = Vote.DOWN;
        }
        else {
            this.vote = Vote.NONE;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public ObjectId get_id() {
        return _id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
