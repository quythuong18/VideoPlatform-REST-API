package com.qt.VideoPlatformAPI.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class APIException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public APIException(String message, HttpStatus status, Throwable exception) {
        super(exception);
        this.message = message;
        this.status = status;
    }
}
