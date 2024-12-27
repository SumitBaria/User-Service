package com.scm.user_service.entity.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

@Data
@NonNull
public class SignUpRequest {

    private String firstName;
    private String lastName;
    private String mobileNumber;
    @Email(message = "Invalid username. It should be email address")
    private String userName;
    private String password;
    private String address;
}
