package com.qt.VideoPlatformAPI.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Inheritance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.swing.text.StyledEditorKit;
import java.lang.annotation.Inherited;

@Getter
@Setter
@AllArgsConstructor
public class AvailabilityResponse extends APIResponse {
    @JsonProperty("isExisted")
    private Boolean isExisted;
    public AvailabilityResponse(Boolean success, String message, HttpStatus httpStatus, Boolean isExisted) {
        super(success, message, httpStatus);
        this.isExisted = isExisted;
    }
}
