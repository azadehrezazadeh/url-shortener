package ir.neshan.urlshortener.exception;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class APIException extends RuntimeException {

    public enum Code {
        BAD_REQUEST,
        IMPOSSIBLE_REQUEST,
        ACCESS_DENIED,
        DUPLICATED,
        NOT_FOUND
    }

    private final Code code;
    private final String message;
}
