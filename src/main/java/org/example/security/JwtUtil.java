package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jws<Claims> claims = parseToken(token);
            String usernameFromToken = claims.getBody().getSubject();
            Date expiration = claims.getBody().getExpiration();
            log.info("Token username: {}", usernameFromToken);
            log.info("Token expiration: {}", expiration);

            boolean isNotExpired = !expiration.before(new Date());
            boolean usernameMatches = usernameFromToken.equals(userDetails.getUsername());

            return isNotExpired && usernameMatches;

        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation failed", e);
            return false;
        }
    }




    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
