package ua.goit.urlshortener.url.exceptions;

public class AlreadyExistUrlException extends RuntimeException{

    private static final String URL_ALREADY_EXIST_TEXT = "Url with short_url = %s already exist";

    public AlreadyExistUrlException(String shortUrl){
        super(String.format(URL_ALREADY_EXIST_TEXT, shortUrl));
    }
}
