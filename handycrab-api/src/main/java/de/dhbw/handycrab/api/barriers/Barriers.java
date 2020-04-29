package de.dhbw.handycrab.api.barriers;

import org.bson.types.ObjectId;

import java.util.List;

public interface Barriers {
    String LOOKUP = "java:app/server/BarriersBean!de.dhbw.handycrab.api.barriers.Barriers";

    FrontendBarrier getBarrier(ObjectId id, ObjectId requesterId);

    List<FrontendBarrier> getBarrier(String postcode, ObjectId requesterId);

    List<FrontendBarrier> getBarrier(double longitude, double latitude, int radius, ObjectId requesterId);

    FrontendBarrier addBarrier(String title, double longitude, double latitude, String postalCode, String description, String solution, ObjectId requesterId);

    FrontendBarrier modifyBarrier(ObjectId id, String title, String description, ObjectId requesterId);

    FrontendBarrier putVote(ObjectId id, Vote vote, ObjectId requesterId);

    FrontendBarrier addSolution(ObjectId id, String solution, ObjectId requesterId);

    FrontendBarrier addVoteToSolution(ObjectId solutionId, Vote vote, ObjectId requesterId);
}
