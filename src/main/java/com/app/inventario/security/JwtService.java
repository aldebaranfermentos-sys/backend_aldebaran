package com.app.inventario.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(String subject, Map<String, String> claims) {
        var now = Instant.now();
        var builder = JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expirationSeconds)));
        if (claims != null) {
            claims.forEach(builder::withClaim);
        }
        return builder.sign(algorithm);
    }

    public String verifyAndGetSubject(String token) {
        var verifier = JWT.require(algorithm).withIssuer(issuer).build();
        var decoded = verifier.verify(token);
        return decoded.getSubject();
    }
}
