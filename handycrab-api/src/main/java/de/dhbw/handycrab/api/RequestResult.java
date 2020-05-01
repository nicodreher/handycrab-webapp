package de.dhbw.handycrab.api;

import java.io.Serializable;

/**
 * The holder to serialize primitive objects as json.
 * @author Nico Dreher
 */
public class RequestResult implements Serializable {
    private Object result;
    public RequestResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
