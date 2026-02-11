package com.example.jwtlogin.dto;

import java.time.Duration;

public record AuthTokens(
        String accessToken,
        Duration accessTtl,
        String refreshToken,
        Duration refreshTtl
) {}
