package com.ecommerce.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.username}")
    private String configuredUsername;

    @Value("${app.jwt.password}")
    private String configuredPassword;

    public AuthController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (!configuredUsername.equals(request.username())
                || !configuredPassword.equals(request.password())) {

            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ecommerce-backend")
                .subject(request.username())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("role", "USER")
                .claim("scope", "orders.write")
                .build();

        String token = jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                JwsHeader.with(MacAlgorithm.HS256).build(),
                                claims
                        )
                )
                .getTokenValue();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType", "Bearer",
                "expiresIn", 3600
        ));
    }

    public record LoginRequest(String username, String password) {
    }
}