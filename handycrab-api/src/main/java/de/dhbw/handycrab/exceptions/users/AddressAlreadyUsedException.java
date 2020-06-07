package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if an user tries to register with an E-Mail address, which ist already used by an other account
 *
 * @author Nico Dreher
 */
public class AddressAlreadyUsedException extends HandyCrabException {
    public AddressAlreadyUsedException() {
        super(400, 3);
    }
}
