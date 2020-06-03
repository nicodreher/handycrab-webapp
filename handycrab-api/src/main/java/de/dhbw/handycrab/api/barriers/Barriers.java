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
     * @return The Barrier
     */
    Barrier getBarrier(ObjectId id);

    /**
     * Get a list of barriers based on the userId of the requester
     * @param requesterId ObjectId of the requester
     * @return List of barriers, where the requesterId equals the userId of the creator of a barrier.
     */
    List<Barrier> getBarrierOnUserId(ObjectId requesterId);

    /**
     * Get a list of barriers based on a postal code
     * @param postcode
     * @return List of barriers with the given postal code.
     */
    List<Barrier> getBarrier(String postcode);

    /**
     * Get a list of barriers based on a position (longitude, latitude) and radius
     * @param longitude
     * @param latitude
     * @param radius
     * @return List of barriers within the radius of the given position
     */
    List<Barrier> getBarrier(double longitude, double latitude, int radius, boolean toDelete);

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
    Barrier addBarrier(String title, double longitude, double latitude, String picture, String postalCode, String description, String solution, ObjectId requesterId);

    /**
     * Modify a barrier
     * @param id ObjectId of the barrier
     * @param title
     * @param picture
     * @param description
     * @param requesterId ObjectId of the requester
     * @return The modified barrier
     */
    Barrier modifyBarrier(ObjectId id, String title, String picture, String description, ObjectId requesterId);

    /**
     * Adds a vote to a barrier
     * @param id ObjectId of the barrier
     * @param vote Value of {@link Vote} enum
     * @param requesterId ObjectId of the requester
     * @return The barrier, which had been voted
     */
    Barrier addVoteToBarrier(ObjectId id, Vote vote, ObjectId requesterId);

    /**
     * Adds a solution to a barrier
     * @param id ObjectId of the barrier
     * @param solution Text of the solution
     * @param requesterId ObjectId of the requester
     * @return The barrier of the added solution
     */
    Barrier addSolution(ObjectId id, String solution, ObjectId requesterId);

    /**
     * Adds a vote to a solution
     * @param solutionId ObjectId of the solution
     * @param vote Value of {@link Vote} enum
     * @param requesterId ObjectId of the requester
     * @return The barrier of the solution
     */
    Barrier addVoteToSolution(ObjectId solutionId, Vote vote, ObjectId requesterId);

    /**
     * Deletes a barrier
     * @param id ObjectId of the barrier
     * @param requesterId ObjectId of the requester
     * @return true, if barrier was deleted
     */
    boolean deleteBarrier(ObjectId id, ObjectId requesterId);

    /**
     * Adds a comment to a existing barrier
     * @param barrierId ObjectId of the barrier
     * @param comment comment as string
     * @param requesterId ObjectId of the requester
     * @return The barrier with the given barrierId
     */
    Barrier addCommentToBarrier(ObjectId barrierId, String comment, ObjectId requesterId);

    /**
     * Marks a barrier for deletion.
     * @param barrierId ObjectId of the barrier
     * @param requesterId ObjectId of the requester
     * @return true, if marked
     */
    boolean markBarrierForDeletion(ObjectId barrierId, ObjectId requesterId);
}
