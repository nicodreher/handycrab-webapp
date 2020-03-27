package de.dhbw.handycrab.server.test;

import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import de.dhbw.handycrab.server.test.mongo.MongoTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestServiceTest extends MongoTest {


    @BeforeEach
    public void setupTest() {
        System.out.println("SETUP TEST");
    }

    @AfterEach
    public void killTest() {
        System.out.println("Kill TEST");
    }

    @Test
    public void testTest() {
        System.out.println("Test");
        assertEquals(1, 1);
    }

    @Test
    public void containerStartsAndPublicPortIsAvailable() {
        assertThatPortIsAvailable(container);
    }

    private void assertThatPortIsAvailable(MongoContainer container) {
        try {
            new Socket(container.getContainerIpAddress(), container.getPort());
        } catch (IOException e) {
            throw new AssertionError("The expected port " + container.getPort() + " is not available!");
        }
    }

}
