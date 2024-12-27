package com.scm.user_service.entity.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
public class LoginResponse {
    private UUID id;
    private String token;
    private Date tokenExpirationTime;
    private String tokenType;
    private String refreshToken;
}
