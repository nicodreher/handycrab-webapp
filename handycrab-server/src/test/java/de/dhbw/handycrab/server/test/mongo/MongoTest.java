package de.dhbw.handycrab.server.test.mongo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoTest {

    @Container
    protected MongoContainer container = new MongoContainer();
}
