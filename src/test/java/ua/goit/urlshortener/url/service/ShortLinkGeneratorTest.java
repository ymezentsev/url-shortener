package ua.goit.urlshortener.url.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortLinkGeneratorTest {

    @Test
    @DisplayName("Generate new short url")
    void generateShortLinkTest() {
        ShortLinkGenerator shortLinkGenerator = new ShortLinkGenerator();
        assertEquals(8, shortLinkGenerator.generateShortLink().length());
    }
}