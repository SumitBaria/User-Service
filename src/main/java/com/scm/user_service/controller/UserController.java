package com.scm.user_service.controller;

import com.scm.user_service.entity.request.AuthenticationRequest;
import com.scm.user_service.entity.request.LoginRequest;
import com.scm.user_service.entity.request.RefreshTokenRequest;
import com.scm.user_service.entity.request.SignUpRequest;
import com.scm.user_service.entity.response.AuthenticationResponse;
import com.scm.user_service.entity.response.LoginResponse;
import com.scm.user_service.entity.response.RefreshTokenResponse;
import com.scm.user_service.entity.response.UserResponse;
import com.scm.user_service.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class UserController {

    private final UserAuthService userAuthService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userAuthService.login(loginRequest));
    }

    @PostMapping(value = "/refreshToken")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest requestTokenRequest) {
        RefreshTokenResponse refreshTokenResponse = userAuthService.refreshToken(requestTokenRequest);
        return ResponseEntity.ok(refreshTokenResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<UUID> singup(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userAuthService.signUp(signUpRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        try {
            boolean authenticated = userAuthService.authenticate(authenticationRequest);
            return authenticated ? ResponseEntity.ok(AuthenticationResponse.builder().message("Successfully Authenticated").isAuthenticated(Boolean.TRUE).build()) : ResponseEntity.status(429).body(AuthenticationResponse.builder().message("Forbidden").build());
        } catch (UsernameNotFoundException usernameNotFoundException) {
            log.error("UserName Not Found: ", usernameNotFoundException);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthenticationResponse.builder().message("UserName Not Found").build());
        }

    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> getProfile(@RequestHeader(name = "Authorization") String token, @PathVariable String userId) {
        return ResponseEntity.ok(userAuthService.getProfile(userId, token));
    }

}
