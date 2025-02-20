package com.qt.VideoPlatformAPI.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class AvailabilityResponse {
    @JsonProperty("isExisted")
    private Boolean isExisted;
}
