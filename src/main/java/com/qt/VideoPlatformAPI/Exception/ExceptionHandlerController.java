package com.qt.VideoPlatformAPI.Exception;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(FileUploadException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIResponse> resolveFileUploadException(FileUploadException exception) {
        String message = exception.getMessage();
        APIResponse response = new APIResponse(Boolean.FALSE, message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponseWithData<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new APIResponseWithData<Map<String, String>>(
                Boolean.FALSE, "Missing field", HttpStatus.BAD_REQUEST, errors
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<APIResponse> resolveInternalServerError(FileUploadException exception) {
        String message = exception.getMessage();
        APIResponse response = new APIResponse(Boolean.FALSE, message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
