package de.dhbw.handycrab.server.exceptions;

public class BarrierNotFoundException extends HandyCrabException {
    public BarrierNotFoundException() {
        super(404, 9);
    }
}
