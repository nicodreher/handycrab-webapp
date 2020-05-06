package de.dhbw.handycrab.exceptions.barriers;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a client requests a non existing solution.
 */
public class SolutionNotFoundException extends HandyCrabException {
    public SolutionNotFoundException() {
        super(404, 11);
    }
}
