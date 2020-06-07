package de.dhbw.handycrab.exceptions.users;

import de.dhbw.handycrab.exceptions.HandyCrabException;

/**
 * Thrown if an user tries to register with an username, which ist already used by an other account
 *
 * @author Nico Dreher
 */
public class NameAlreadyUsedException extends HandyCrabException {
    public NameAlreadyUsedException() {
        super(400, 4);
    }
}
