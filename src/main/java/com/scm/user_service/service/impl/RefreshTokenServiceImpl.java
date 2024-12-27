package com.scm.user_service.service.impl;

import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;
import com.scm.user_service.repository.RefreshTokenRepository;
import com.scm.user_service.service.RefreshTokenService;
import com.scm.user_service.utils.DateTimeUtils;
import com.scm.user_service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.scm.user_service.constant.CommonConstants.REFRESH_TOKEN_NOT_FOUND_EXCEPTION;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${security.jwt.refresh-time}")
    private Long refreshTokenExpirationMS;

    @Override
    public RefreshToken saveRefreshToken(User user) {
        RefreshToken refreshToken = build(user);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException(REFRESH_TOKEN_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<RefreshToken> getRefreshToken(User user) {
        return refreshTokenRepository.findByUser(user);
    }

    @Override
    public void deleteExpiredRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private RefreshToken build(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMS));
        refreshToken.setCreatedOn(DateTimeUtils.getCurrentSQLTimestamp());
        return refreshToken;
    }
}
