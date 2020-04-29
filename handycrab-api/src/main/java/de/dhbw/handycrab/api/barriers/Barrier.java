package de.dhbw.handycrab.api.barriers;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Barrier implements Serializable {

    private ObjectId _id;
    private ObjectId userId;
    private String title;
    private double longitude;
    private double latitude;
    private Point point;
    private URL picture;
    private String description;
    private String postcode;
    private List<Solution> solutions = new ArrayList<>();
    private List<ObjectId> upVotes = new ArrayList<>();
    private List<ObjectId> downVotes = new ArrayList<>();

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
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

    public void setPostCode(String postcode) {
        this.postcode = postcode;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public ObjectId get_id() {
        return _id;
    }

    public List<ObjectId> getUpVotes() {
        return upVotes;
    }

    public List<ObjectId> getDownVotes() {
        return downVotes;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongAndLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.point = new Point(new Position(longitude, latitude));
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        _id = (ObjectId) objectInputStream.readObject();
        userId = (ObjectId) objectInputStream.readObject();
        title = objectInputStream.readUTF();
        longitude = objectInputStream.readDouble();
        latitude = objectInputStream.readDouble();
        point = new Point(new Position(longitude, latitude));
        description = objectInputStream.readUTF();
        postcode = objectInputStream.readUTF();
        solutions = (List<Solution>) objectInputStream.readObject();
        upVotes = (List<ObjectId>) objectInputStream.readObject();
        downVotes = (List<ObjectId>) objectInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(_id);
        stream.writeObject(userId);
        stream.writeUTF(title);
        stream.writeDouble(longitude);
        stream.writeDouble(latitude);
        stream.writeUTF(description);
        stream.writeUTF(postcode);
        stream.writeObject(solutions);
        stream.writeObject(upVotes);
        stream.writeObject(downVotes);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
