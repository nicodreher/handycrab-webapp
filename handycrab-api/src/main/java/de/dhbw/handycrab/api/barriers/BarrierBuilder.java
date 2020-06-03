package de.dhbw.handycrab.api.barriers;

import org.bson.types.ObjectId;

/**
 * Builder pattern for the {@link Barrier} class.
 * @author Lukas Lautenschlager
 */
public class BarrierBuilder {
    Barrier barrier = new Barrier();

    public BarrierBuilder title(String title) {
        barrier.setTitle(title);
        return this;
    }

    public BarrierBuilder point(double longitude, double latitude) {
        barrier.setLongAndLat(longitude, latitude);
        return this;
    }

    public BarrierBuilder postalCode(String postalCode) {
        barrier.setPostCode(postalCode);
        return this;
    }

    public BarrierBuilder description(String description) {
        barrier.setDescription(description);
        return this;
    }

    public BarrierBuilder userId(ObjectId userId) {
        barrier.setUserId(userId);
        return this;
    }

    public BarrierBuilder solution(Solution solution) {
        barrier.getSolutions().add(solution);
        return this;
    }

    public BarrierBuilder picture(ObjectId picture) {
        barrier.setPicture(picture);
        return this;
    }

    public BarrierBuilder comment(Comment comment)
    {
        barrier.addComment(comment.getComment(), comment.getUserId());
        return this;
    }

    public Barrier build() {
        return barrier;
    }
}
