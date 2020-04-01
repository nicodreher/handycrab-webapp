package de.dhbw.handycrab.api;

import java.io.Serializable;

public class RequestResult implements Serializable {
    private Object result;
    public RequestResult(Object result) {
        this.result = result;
    }
}
