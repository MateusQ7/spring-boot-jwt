package com.example.jwtlogin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;
    private final Key jwtSigningKey;

    public String generateAccessToken(Long userId, String email, List<String> roles){
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTokenMinutes() * 60);

        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .requireIssuer(props.issuer())
                .build()
                .parseClaimsJws(token);
    }
}
