package com.scm.user_service.service;

import com.scm.user_service.entity.request.AuthenticationRequest;
import com.scm.user_service.entity.request.LoginRequest;
import com.scm.user_service.entity.request.RefreshTokenRequest;
import com.scm.user_service.entity.request.SignUpRequest;
import com.scm.user_service.entity.response.LoginResponse;
import com.scm.user_service.entity.response.RefreshTokenResponse;
import com.scm.user_service.entity.response.UserResponse;

import java.util.UUID;

public interface UserAuthService {

    LoginResponse login(LoginRequest loginRequest);

    UUID signUp(SignUpRequest signUpRequest);

    boolean authenticate(AuthenticationRequest authenticationRequest);

    UserResponse getProfile(String userId, String token);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
