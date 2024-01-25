package ua.goit.urlshortener.url.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class ShortLinkGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LINK_LENGTH = 8;

    public String generateShortLink() {
        Random random = new SecureRandom();

        StringBuilder linkBuilder = new StringBuilder();
        for (int i = 0; i < LINK_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            linkBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return linkBuilder.toString();
    }
}
