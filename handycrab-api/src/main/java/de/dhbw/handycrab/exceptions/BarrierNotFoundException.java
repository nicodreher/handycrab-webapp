package de.dhbw.handycrab.exceptions;

public class BarrierNotFoundException extends HandyCrabException {
    public BarrierNotFoundException() {
        super(404, 9);
    }
}
