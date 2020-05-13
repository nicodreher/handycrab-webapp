package de.dhbw.handycrab.server.test.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import de.dhbw.handycrab.exceptions.HandyCrabException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Nico Dreher
 */
class ExceptionsTest {

    /**
     * Testing if the errorCode and http statusCode of the {@link HandyCrabException} matches with the specification
     * @param exception The class of the exception
     * @param statusCode The expected http status code
     * @param errorCode The expected handycrab error code
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @ParameterizedTest(name = "[{index}] Exception: {0} Http Status Code: {1} Handycrab Error Code: {2}")
    @CsvFileSource(resources = "/exceptions/exceptions.csv")
    void exceptionsTest(String exception, int statusCode, int errorCode) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(exception);
        Object instance = clazz.getConstructor().newInstance();
        assertTrue(instance instanceof HandyCrabException);
        HandyCrabException e = (HandyCrabException) instance;
        assertEquals(statusCode, e.getStatusCode());
        assertEquals(errorCode, e.getErrorCode());
    }
}
