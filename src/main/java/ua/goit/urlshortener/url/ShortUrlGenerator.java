package ua.goit.urlshortener.url;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ShortUrlGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 8;

    public String generateShortUrl() {
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int randomIndex = new Random().nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
