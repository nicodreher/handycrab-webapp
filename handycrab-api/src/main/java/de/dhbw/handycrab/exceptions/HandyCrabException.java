package de.dhbw.handycrab.exceptions;

/**
 * HandyCrabExceptions can be thrown to respond to a request with an error code
 * @author Nico Dreher
 */
public class HandyCrabException extends RuntimeException {
    /**
     * The http status code of the response
     */
    private int statusCode;
    /**
     * The HandyCrab error code in the response
     * {errocode: $value}
     */
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
