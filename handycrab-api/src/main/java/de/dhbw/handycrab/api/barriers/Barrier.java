package de.dhbw.handycrab.api.barriers;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of a barrier
 * @author Lukas Lautenschlager
 */
public class Barrier implements Serializable {

    private ObjectId _id;
    private ObjectId userId;
    private String title;
    private double longitude;
    private double latitude;
    private Point point;
    private ObjectId picture = null;
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

    public ObjectId getPicture() {
        return picture;
    }

    public void setPicture(ObjectId picture) {
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     * Helper method to create a BSON Point based on a given longitude and latitude.
     * @param longitude Longitude
     * @param latitude Latitude
     */
    public void setLongAndLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.point = new Point(new Position(longitude, latitude));
    }
    /**
     * Method for the deserialization of a barrier.
     * Used to enable geospatial queries on longitude and latitude.
     * @param objectInputStream - InputStream
     * @throws ClassNotFoundException
     * @throws IOException
    */
    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        _id = (ObjectId) objectInputStream.readObject();
        userId = (ObjectId) objectInputStream.readObject();
        title = objectInputStream.readUTF();
        picture = (ObjectId) objectInputStream.readObject();
        longitude = objectInputStream.readDouble();
        latitude = objectInputStream.readDouble();
        point = new Point(new Position(longitude, latitude));
        description = objectInputStream.readUTF();
        postcode = objectInputStream.readUTF();
        solutions = (List<Solution>) objectInputStream.readObject();
        upVotes = (List<ObjectId>) objectInputStream.readObject();
        downVotes = (List<ObjectId>) objectInputStream.readObject();
    }

    /**
     * Method for the serialization of a barrier.
     * Used to enable geospatial queries on longitude and latitude on the Mongo DB
     * and excluding point to implement the {@link Serializable} interface, which is needed for the EJB bean.
     * @param stream - OutputStream
     * @throws IOException
    */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(_id);
        stream.writeObject(userId);
        stream.writeUTF(title);
        stream.writeObject(picture);
        stream.writeDouble(longitude);
        stream.writeDouble(latitude);
        stream.writeUTF(description);
        stream.writeUTF(postcode);
        stream.writeObject(solutions);
        stream.writeObject(upVotes);
        stream.writeObject(downVotes);
    }
}
