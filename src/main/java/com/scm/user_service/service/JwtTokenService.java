package com.scm.user_service.service;

import com.scm.user_service.entity.JwtAccessToken;
import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;

public interface JwtTokenService {

    JwtAccessToken saveJwtToken(User user);

    JwtAccessToken generateJwtToken(User user, RefreshToken refreshToken);


}
