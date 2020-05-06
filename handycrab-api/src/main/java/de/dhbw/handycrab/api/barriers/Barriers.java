package de.dhbw.handycrab.api.barriers;

import de.dhbw.handycrab.api.RequestResult;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * EJB interface for the administration of barriers.
 * @author Lukas Lautenschlager
 */
public interface Barriers {
    String LOOKUP = "java:app/server/BarriersBean!de.dhbw.handycrab.api.barriers.Barriers";

    /**
     * Get a barrier based on the ObjectId
     * @param id ObjectId of the barrier
     * @param requesterId ObjectId of the requester
     * @return The Barrier
     */
    FrontendBarrier getBarrier(ObjectId id, ObjectId requesterId);

    /**
     * Get a list of barriers based on the userId of the requester
     * @param requesterId ObjectId of the requester
     * @return List of barriers, where the requesterId equals the userId of the creator of a barrier.
     */
    List<FrontendBarrier> getBarrier(ObjectId requesterId);

    /**
     * Get a list of barriers based on a postal code
     * @param postcode
     * @param requesterId ObjectId of the requester
     * @return List of barriers with the given postal code.
     */
    List<FrontendBarrier> getBarrier(String postcode, ObjectId requesterId);

    /**
     * Get a list of barriers based on a position (longitude, latitude) and radius
     * @param longitude
     * @param latitude
     * @param radius
     * @param requesterId ObjectId of the requester
     * @return List of barriers within the radius of the given position
     */
    List<FrontendBarrier> getBarrier(double longitude, double latitude, int radius, ObjectId requesterId);

    /**
     * Adds a barrier
     * @param title
     * @param longitude
     * @param latitude
     * @param picture Picture encoded in Base64
     * @param postalCode
     * @param description
     * @param solution
     * @param requesterId
     * @return The added barrier
     */
    FrontendBarrier addBarrier(String title, double longitude, double latitude, String picture, String postalCode, String description, String solution, ObjectId requesterId);

    /**
     * Modify a barrier
     * @param id ObjectId of the barrier
     * @param title
     * @param picture
     * @param description
     * @param requesterId ObjectId of the requester
     * @return The modified barrier
     */
    FrontendBarrier modifyBarrier(ObjectId id, String title, String picture, String description, ObjectId requesterId);

    /**
     * Adds a vote to a barrier
     * @param id ObjectId of the barrier
     * @param vote Value of {@link Vote} enum
     * @param requesterId ObjectId of the requester
     * @return The barrier, which had been voted
     */
    FrontendBarrier addVoteToBarrier(ObjectId id, Vote vote, ObjectId requesterId);

    /**
     * Adds a solution to a barrier
     * @param id ObjectId of the barrier
     * @param solution Text of the solution
     * @param requesterId ObjectId of the requester
     * @return The barrier of the added solution
     */
    FrontendBarrier addSolution(ObjectId id, String solution, ObjectId requesterId);

    /**
     * Adds a vote to a solution
     * @param solutionId ObjectId of the solution
     * @param vote Value of {@link Vote} enum
     * @param requesterId ObjectId of the requester
     * @return The barrier of the solution
     */
    FrontendBarrier addVoteToSolution(ObjectId solutionId, Vote vote, ObjectId requesterId);

    /**
     * Deletes a barrier
     * @param id ObjectId of the barrier
     * @param requesterId ObjectId of the requester
     * @return true, if barrier was deleted
     */
    RequestResult deleteBarrier(ObjectId id, ObjectId requesterId);
}
