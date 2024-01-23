package ua.goit.urlshortener.url.exceptions;

public class NotAccessibleException extends RuntimeException{
    private static final String URL_IS_NOT_ACCESSIBLE_TEXT = "Url with url = %s is not accessible!";

    public NotAccessibleException(String url){
        super(String.format(URL_IS_NOT_ACCESSIBLE_TEXT, url));
    }
}