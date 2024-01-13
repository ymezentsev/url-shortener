package ua.goit.urlshortener.link;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ShortLinkGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_LINK_LENGTH = 8;

    public String generateShortLink() {
        StringBuilder sb = new StringBuilder(SHORT_LINK_LENGTH);

        for (int i = 0; i < SHORT_LINK_LENGTH; i++) {
            int randomIndex = new Random().nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
