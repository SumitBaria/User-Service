package com.scm.user_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

import static com.scm.user_service.constant.CommonConstants.DATE_TIME_FORMAT;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "jwt_access_token")
public class JwtAccessToken extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @OneToOne
    @JoinColumn(name = "refresh_token_id", nullable = false, unique = true)
    private RefreshToken refreshToken;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private Timestamp expireDate;

    @Column(nullable = false, unique = true)
    private String tokenType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
