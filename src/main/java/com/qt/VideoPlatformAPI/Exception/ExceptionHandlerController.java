package com.qt.VideoPlatformAPI.Exception;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {
    public ResponseEntity<APIResponse> resolveException(APIException exception) {
        String message = exception.getMessage();
        HttpStatus httpStatus = exception.getStatus();

        APIResponse response = new APIResponse(Boolean.FALSE, message, httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<APIResponse> resolveUsernameNotFoundException(UsernameNotFoundException exception) {
        String message = exception.getMessage();
        APIResponse response = new APIResponse(Boolean.FALSE, message, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIResponse> resolveIllegalException(IllegalArgumentException exception) {
        String message = exception.getMessage();
        APIResponse response = new APIResponse(Boolean.FALSE, message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
