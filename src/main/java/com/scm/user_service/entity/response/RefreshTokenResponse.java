package com.scm.user_service.entity.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenResponse {

    private String token;
    private Date tokenExpirationTime;
    private String tokenType;
    private String uri;
    private String message;
}
