package de.dhbw.handycrab.server.test.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import de.dhbw.handycrab.server.exceptions.HandyCrabException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.InvocationTargetException;

public class Exceptions {

    @ParameterizedTest(name = "[{index}] Exception: {0} Http Status Code: {1} Handycrab Error Code: {2}")
    @CsvFileSource(resources = "/exceptions/exceptions.csv")
    public void exceptionsTest(String exception, int statusCode, int errorCode) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(exception);
        Object instance = clazz.getConstructor().newInstance();
        assertTrue(instance instanceof HandyCrabException);
        HandyCrabException e = (HandyCrabException) instance;
        assertEquals(statusCode, e.getStatusCode());
        assertEquals(errorCode, e.getErrorCode());
    }
}
