package de.dhbw.handycrab.api.barriers;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of a solution.
 *
 * @author Lukas Lautenschlager
 */
public class Solution implements Serializable {
    private ObjectId _id = new ObjectId();
    private String text;
    private ObjectId userId;
    private List<ObjectId> upVotes = new ArrayList<>();
    private List<ObjectId> downVotes = new ArrayList<>();

    public ObjectId getId() {
        return _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public List<ObjectId> getUpVotes() {
        return upVotes;
    }

    public List<ObjectId> getDownVotes() {
        return downVotes;
    }
}
