package com.qt.VideoPlatformAPI.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class APIResponseWithData<T> extends APIResponse {
    private T data;
    public APIResponseWithData(Boolean success, String message, HttpStatus httpStatus, T data) {
        super(success, message, httpStatus);
        this.data = data;
    }
}
