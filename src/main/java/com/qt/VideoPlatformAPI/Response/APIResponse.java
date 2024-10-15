package com.qt.VideoPlatformAPI.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Inheritance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.lang.annotation.Inherited;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "success",
        "message"
})
public class APIResponse {
    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("httpStatus")
    private HttpStatus httpStatus;

}
