package com.qt.VideoPlatformAPI.Utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse extends APIResponse {
     private String token;
}
