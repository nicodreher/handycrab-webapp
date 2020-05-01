package de.dhbw.handycrab.exceptions;

public class SolutionNotFoundException extends HandyCrabException {
    public SolutionNotFoundException() {
        super(404, 11);
    }
}
