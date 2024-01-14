package ua.goit.urlshortener.url;

import lombok.Data;

@Data
public class CreateShotUrlRequest {
    private String originalUrl;
}
