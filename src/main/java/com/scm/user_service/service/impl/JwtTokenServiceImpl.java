package com.scm.user_service.service.impl;

import com.scm.user_service.constant.CommonConstants;
import com.scm.user_service.entity.JwtAccessToken;
import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;
import com.scm.user_service.repository.JwtAccessTokenRepository;
import com.scm.user_service.service.JwtTokenService;
import com.scm.user_service.service.RefreshTokenService;
import com.scm.user_service.utils.DateTimeUtils;
import com.scm.user_service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final JwtAccessTokenRepository jwtAccessTokenRepository;

    @Override
    public JwtAccessToken saveJwtToken(User user) {
        return refreshTokenService
                .getRefreshToken(user)
                .map(refreshToken -> validateAndCreateToken(user, refreshToken))
                .orElseGet(() -> validateAndCreateToken(user, refreshTokenService.saveRefreshToken(user)));

    }

    private JwtAccessToken validateAndCreateToken(User user, RefreshToken refreshToken) {
        Optional<JwtAccessToken> jwtAccessTokenOpt = jwtAccessTokenRepository.findByUser(user);
        if(jwtAccessTokenOpt.isPresent() && jwtAccessTokenOpt.get().getExpireDate().after(DateTimeUtils.getCurrentSQLTimestamp())) {
            return jwtAccessTokenOpt.get();
        } else {
            jwtAccessTokenRepository.deleteByUser(user);
            return saveToken(user, refreshToken);
        }
    }

    private JwtAccessToken saveToken(User user, RefreshToken refreshToken) {
        String token = jwtUtils.generateTokenFromUserName(user.getUserName());
        return jwtAccessTokenRepository
                .save(buildJwtToken(user, token, refreshToken));
    }

    @Override
    @Transactional
    public JwtAccessToken generateJwtToken(User user, RefreshToken refreshToken) {
        jwtAccessTokenRepository.deleteByUser(user);
        String token = jwtUtils.generateTokenFromUserName(user.getUserName());
        JwtAccessToken accessToken = buildJwtToken(user, token, refreshToken);
        return jwtAccessTokenRepository.save(accessToken);
    }

    private JwtAccessToken buildJwtToken(User user, String token, RefreshToken refreshToken) {
        JwtAccessToken jwtAccessToken = new JwtAccessToken();
        jwtAccessToken.setAccessToken(token);
        jwtAccessToken.setRefreshToken(refreshToken);
        jwtAccessToken.setUser(user);
        jwtAccessToken.setExpireDate(DateTimeUtils.getSQLTimestamp(jwtUtils.extractExpiration(token)));
        jwtAccessToken.setTokenType(CommonConstants.TOKEN_TYPE);
        return jwtAccessToken;
    }
}
