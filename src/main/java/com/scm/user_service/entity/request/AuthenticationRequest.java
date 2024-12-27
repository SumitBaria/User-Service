package com.scm.user_service.entity.request;

import lombok.Data;
import lombok.NonNull;

@Data
@NonNull
public class AuthenticationRequest {

    private String token;
}
