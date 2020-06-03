package de.dhbw.handycrab.api.barriers;

import org.bson.types.ObjectId;

import java.io.Serializable;

public class Comment implements Serializable {
    private ObjectId userId;
    private String comment;

    public Comment(ObjectId userId, String comment)
    {
        this.comment = comment;
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }
}
