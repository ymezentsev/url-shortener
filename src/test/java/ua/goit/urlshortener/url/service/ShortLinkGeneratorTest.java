package ua.goit.urlshortener.url.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortLinkGeneratorTest {

    @Test
    void generateShortLinkTest() {
        ShortLinkGenerator shortLinkGenerator = new ShortLinkGenerator();
        assertEquals(8, shortLinkGenerator.generateShortLink().length());
    }
}