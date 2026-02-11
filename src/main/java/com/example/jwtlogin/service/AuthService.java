package com.example.jwtlogin.service;

import com.example.jwtlogin.dto.AuthTokens;
import com.example.jwtlogin.exception.UnauthorizedException;
import com.example.jwtlogin.model.RefreshToken;
import com.example.jwtlogin.model.User;
import com.example.jwtlogin.repository.RefreshTokenRepository;
import com.example.jwtlogin.repository.UserRepository;
import com.example.jwtlogin.security.JwtProperties;
import com.example.jwtlogin.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties props;
    private final TokenUtils tokenUtils;

    @Transactional
    public AuthTokens login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthTokens refresh(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new UnauthorizedException("Refresh token ausente");
        }

        String hash = tokenUtils.sha256Hex(refreshTokenRaw);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (token.isRevoked() || token.isExpired(now)) {
            throw new UnauthorizedException("Refresh token inválido");
        }

        token.setRevokedAt(now);
        refreshTokenRepository.save(token);

        User user = token.getUser();
        if (!user.isActive()) {
            throw new UnauthorizedException("Usuário inativo");
        }

        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) return;

        String hash = tokenUtils.sha256Hex(refreshTokenRaw);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            if (!rt.isRevoked()) {
                rt.setRevokedAt(LocalDateTime.now(ZoneOffset.UTC));
                refreshTokenRepository.save(rt);
            }
        });
    }

    private AuthTokens issueTokens(User user) {
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), List.of(user.getRole()));
        Duration accessTtl = Duration.ofMinutes(props.accessTokenMinutes());

        String refreshRaw = tokenUtils.newOpaqueToken();
        String refreshHash = tokenUtils.sha256Hex(refreshRaw);
        Duration refreshTtl = Duration.ofDays(props.refreshTokenDays());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHash(refreshHash)
                .createdAt(now)
                .expiresAt(now.plus(refreshTtl))
                .revokedAt(null)
                .build();
        refreshTokenRepository.save(rt);

        return new AuthTokens(access, accessTtl, refreshRaw, refreshTtl);
    }
}
