package com.scm.user_service.repository;

import com.scm.user_service.entity.JwtAccessToken;
import com.scm.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JwtAccessTokenRepository extends JpaRepository<JwtAccessToken, Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteByUser(User user);

    Optional<JwtAccessToken> findByUser(User user);
}