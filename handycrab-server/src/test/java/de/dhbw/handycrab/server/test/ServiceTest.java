package de.dhbw.handycrab.server.test;

import de.dhbw.handycrab.server.test.mongo.MongoContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class ServiceTest {

    @Container
    private MongoContainer container = new MongoContainer();

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
