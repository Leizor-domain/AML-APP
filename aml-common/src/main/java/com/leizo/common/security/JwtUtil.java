package com.leizo.common.security;

/**
 * JwtUtil is a Spring bean. To use it, ensure the following properties are set in your module's application.properties:
 *   jwt.secret, jwt.expiration, jwt.audience
 * Do NOT include these properties in modules that do not require JWT authentication.
 */
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.audience}")
    private String audience;

    private static final String ISSUER = "aml-engine";

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setIssuer(ISSUER)
                .setAudience(audience)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public Claims validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        // Validate standard claims
        if (!ISSUER.equals(claims.getIssuer())) {
            throw new JwtException("Invalid issuer");
        }
        if (!audience.equals(claims.getAudience())) {
            throw new JwtException("Invalid audience");
        }
        return claims;
    }

    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}


