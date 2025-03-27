package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.TokenRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret_key}")
    private String secretKey;
    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;

    private  TokenRepo tokenRepo;

    public JwtService (TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }
    private SecretKey getSigningKey() {

//        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);

        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    private String generateToken(User user, Long expiryTime) {

        JwtBuilder builder = Jwts.builder()
                // Установка субъекта токена (имя пользователя)
                .subject(user.getUsername())
                // (текущая дата)
                .issuedAt(new Date(System.currentTimeMillis()))
                // (текущая дата + 10 часов)
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey());


        return builder.compact();
    }
    public String generateAccessToken(User user) {

        return generateToken(user, accessTokenExpiration);
    }
    public String generateRefreshToken(User user) {

        return generateToken(user, refreshTokenExpiration);
    }

    private Claims extractAllClaims(String token) {

        JwtParserBuilder parser = Jwts.parser();

        // проверка ключа
        parser.verifyWith(getSigningKey());

        // извлечь
        return parser.build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    public String extractName(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isAccessTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }
    // проверка валидности аксестокена
    public boolean isValid(String token, UserDetails user) {

        String username = extractName(token);

        boolean isValidToken = tokenRepo.findByAccessToken(token).map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenExpired(token)
                && isValidToken;
    }
    // проверка рефреша
    public boolean isValidRefreshToken(String token, UserDetails user) {

        String username = extractName(token);

        boolean isValidRefreshToken = tokenRepo.findByRefreshToken(token).
                map(t-> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenExpired(token)
                && isValidRefreshToken;
    }
}
