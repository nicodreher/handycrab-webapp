package de.dhbw.handycrab.server.exceptions;

public class SolutionNotFoundException extends HandyCrabException {
    public SolutionNotFoundException() {
        super(404, 11);
    }
}
