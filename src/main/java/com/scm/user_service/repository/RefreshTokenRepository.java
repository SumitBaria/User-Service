package com.scm.user_service.repository;

import com.scm.user_service.entity.RefreshToken;
import com.scm.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteByToken(String token);
}