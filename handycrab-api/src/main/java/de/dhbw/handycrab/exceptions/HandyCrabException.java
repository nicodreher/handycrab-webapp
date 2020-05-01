package de.dhbw.handycrab.exceptions;

public class HandyCrabException extends RuntimeException {
    private int statusCode;
    private int errorCode;

    protected HandyCrabException(int statusCode, int errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
