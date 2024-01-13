package ua.goit.urlshortener.link;

import lombok.Data;

@Data
public class CreateShotLinkRequest {
    private String originalLink;
}
