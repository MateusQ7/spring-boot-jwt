package com.example.jwtlogin.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookies {

    private final JwtProperties props;

    public AuthCookies(JwtProperties props) {
        this.props = props;
    }

    public void setAccessToken(HttpServletResponse res, String token, Duration maxAge) {
        addCookie(res, props.cookieAccessName(), token, maxAge);
    }

    public void setRefreshToken(HttpServletResponse res, String token, Duration maxAge) {
        addCookie(res, props.cookieRefreshName(), token, maxAge);
    }

    public void clearAuthCookies(HttpServletResponse res) {
        addCookie(res, props.cookieAccessName(), "", Duration.ZERO);
        addCookie(res, props.cookieRefreshName(), "", Duration.ZERO);
    }

    private void addCookie(HttpServletResponse res, String name, String value, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(props.cookieSecure())
                .path("/")
                .sameSite(props.cookieSameSite())
                .maxAge(maxAge);

        if (props.cookieDomain() != null && !props.cookieDomain().isBlank()) {
            b.domain(props.cookieDomain());
        }

        res.addHeader("Set-Cookie", b.build().toString());
    }
}
