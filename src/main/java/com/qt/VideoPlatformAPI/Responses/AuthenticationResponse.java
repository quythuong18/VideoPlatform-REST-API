package com.qt.VideoPlatformAPI.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponse extends APIResponse {
     @JsonProperty("token")
     private String token;
     public AuthenticationResponse(Boolean success, String message, HttpStatus httpStatus, String token) {
          super(success, message, httpStatus);
          this.token = token;
     }
}
