package com.scm.user_service.service.impl;

import com.scm.user_service.advice.exceptions.RequestNotAllowedException;
import com.scm.user_service.constant.CommonConstants;
import com.scm.user_service.entity.JwtAccessToken;
import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;
import com.scm.user_service.entity.UserRole;
import com.scm.user_service.entity.request.AuthenticationRequest;
import com.scm.user_service.entity.request.LoginRequest;
import com.scm.user_service.entity.request.RefreshTokenRequest;
import com.scm.user_service.entity.request.SignUpRequest;
import com.scm.user_service.entity.response.LoginResponse;
import com.scm.user_service.entity.response.RefreshTokenResponse;
import com.scm.user_service.entity.response.UserResponse;
import com.scm.user_service.enums.Role;
import com.scm.user_service.repository.UserRepository;
import com.scm.user_service.repository.UserRoleRepository;
import com.scm.user_service.service.JwtTokenService;
import com.scm.user_service.service.RefreshTokenService;
import com.scm.user_service.service.UserAuthService;
import com.scm.user_service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.scm.user_service.constant.CommonConstants.LOGIN_PAGE_REDIRECTION_URL;
import static com.scm.user_service.constant.CommonConstants.REFRESH_TOKEN_NOT_FOUND_EXCEPTION;
import static com.scm.user_service.constant.CommonConstants.TOKEN_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    private static RefreshTokenResponse buildFailureResponse(String exception) {
        return RefreshTokenResponse
                .builder()
                .message(exception)
                .uri(LOGIN_PAGE_REDIRECTION_URL)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository
                .findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException(CommonConstants.USER_NOT_FOUND));
        JwtAccessToken jwtAccessToken = jwtTokenService.saveJwtToken(user);

        return LoginResponse
                .builder()
                .id(user.getId())
                .token(jwtAccessToken.getAccessToken())
                .tokenExpirationTime(jwtUtils.extractExpiration(jwtAccessToken.getAccessToken()))
                .refreshToken(jwtAccessToken.getRefreshToken().getToken())
                .tokenType(jwtAccessToken.getTokenType())
                .build();
    }

    @Override
    public UUID signUp(SignUpRequest signUpRequest) {
        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserRole userRole = buildUserRole(user, Role.ROLE_USER);
        userRoleRepository.save(userRole);
        return userRepository.save(user).getId();
    }

    private UserRole buildUserRole(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        return userRole;
    }

    @Override
    public boolean authenticate(AuthenticationRequest authenticationRequest) {
        return authenticate(authenticationRequest.getToken());
    }

    private boolean authenticate(String token) {
        String userName = jwtUtils.extractUsername(token);

        if (Objects.nonNull(userName)) {
            userRepository
                    .findByUserName(userName)
                    .orElseThrow(() -> new UsernameNotFoundException(CommonConstants.USER_NOT_FOUND));
            return jwtUtils.isTokenValid(token);
        }

        return false;
    }

    @Override
    public UserResponse getProfile(String userId, String token) {

        if (!authenticate(token.substring(7))) {
            throw new RequestNotAllowedException("Not Authorized");
        }
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UsernameNotFoundException(CommonConstants.USER_NOT_FOUND));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            RefreshToken refreshToken = refreshTokenService
                    .getRefreshToken(refreshTokenRequest.getRefreshToken());
            if (Instant.now().isAfter(refreshToken.getExpiryDate())) {
                refreshTokenService.deleteExpiredRefreshToken(refreshTokenRequest.getRefreshToken());
                return buildFailureResponse(REFRESH_TOKEN_NOT_FOUND_EXCEPTION);
            }

            User user = userRepository
                    .findById(refreshToken.getUser().getId())
                    .orElseThrow(() -> new UsernameNotFoundException(CommonConstants.USER_NOT_FOUND));

            JwtAccessToken jwtAccessToken = jwtTokenService.generateJwtToken(user, refreshToken);
            return RefreshTokenResponse
                    .builder()
                    .token(jwtAccessToken.getAccessToken())
                    .tokenType(jwtAccessToken.getTokenType())
                    .tokenExpirationTime(jwtAccessToken.getExpireDate())
                    .build();

        } catch (RuntimeException exception) {
            log.error("UserAuthServiceImpl:: refreshToken: got runtime exception: ", exception);
            return buildFailureResponse(exception.getMessage());
        }

    }
}
