package com.scm.user_service.service;

import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken saveRefreshToken(User user);

    RefreshToken getRefreshToken(String refreshToken);

    Optional<RefreshToken> getRefreshToken(User user);

    void deleteExpiredRefreshToken(String refreshToken);
}
