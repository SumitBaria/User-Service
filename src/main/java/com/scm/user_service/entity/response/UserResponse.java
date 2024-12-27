package com.scm.user_service.entity.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String userName;
    private String mobileNumber;
    private String address;
}
