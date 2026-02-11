package com.example.jwtlogin.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        String secret,
        long accessTokenMinutes,
        long refreshTokenDays,
        String cookieAccessName,
        String cookieRefreshName,
        boolean cookieSecure,
        String cookieSameSite,
        String cookieDomain
) {}
