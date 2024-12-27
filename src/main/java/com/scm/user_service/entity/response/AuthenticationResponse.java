package com.scm.user_service.entity.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationResponse {

    private String message;
    private boolean isAuthenticated;
}
