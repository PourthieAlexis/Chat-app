package com.example.chat_app.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.chat_app.model.User;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private static final String ISSUER = "self";
    private static final long TOKEN_VALIDITY_DAYS = 1;
	
    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);

		JwtClaimsSet claims = JwtClaimsSet.builder()
					.issuer(ISSUER)
					.issuedAt(now)
					.expiresAt(expiration)
					.subject(user.getUsername())
					.claim("id", user.getId())
					.build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}
