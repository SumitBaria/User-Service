package com.scm.user_service.entity.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Invalid username. It should be email address")
    private String userName;

    private String password;
}
