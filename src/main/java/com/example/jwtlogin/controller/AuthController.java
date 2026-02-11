package com.example.jwtlogin.controller;

import com.example.jwtlogin.dto.LoginRequestDTO;
import com.example.jwtlogin.dto.MessageResponse;
import com.example.jwtlogin.security.AuthCookies;
import com.example.jwtlogin.security.JwtProperties;
import com.example.jwtlogin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookies authCookies;
    private final JwtProperties props;

    @PostMapping("/login")
    public MessageResponse login(@Valid @RequestBody LoginRequestDTO req, HttpServletResponse res) {
        var tokens = authService.login(req.email(), req.password());

        authCookies.setAccessToken(res, tokens.accessToken(), tokens.accessTtl());
        authCookies.setRefreshToken(res, tokens.refreshToken(), tokens.refreshTtl());

        return new MessageResponse("Login realizado com sucesso");
    }

    @PostMapping("/refresh")
    public MessageResponse refresh(HttpServletRequest req, HttpServletResponse res) {
        String refresh = getCookie(req, props.cookieRefreshName());
        var tokens = authService.refresh(refresh);

        authCookies.setAccessToken(res, tokens.accessToken(), tokens.accessTtl());
        authCookies.setRefreshToken(res, tokens.refreshToken(), tokens.refreshTtl());

        return new MessageResponse("Token renovado com sucesso");
    }

    @PostMapping("/logout")
    public MessageResponse logout(HttpServletRequest req, HttpServletResponse res) {
        String refresh = getCookie(req, props.cookieRefreshName());
        authService.logout(refresh);
        authCookies.clearAuthCookies(res);
        return new MessageResponse("Logout realizado com sucesso");
    }

    private String getCookie(HttpServletRequest req, String name) {
        var cookies = req.getCookies();
        if (cookies == null) return null;
        for (var c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
