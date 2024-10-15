package com.qt.VideoPlatformAPI.Response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse extends APIResponse {
     private String token;
}
