package de.dhbw.handycrab.exceptions.barriers;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if a client requests a non existing barrier
 */
public class BarrierNotFoundException extends HandyCrabException {
    public BarrierNotFoundException() {
        super(404, 9);
    }
}
