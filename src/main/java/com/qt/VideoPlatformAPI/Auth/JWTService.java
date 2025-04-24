package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Utils.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JWTService {
    private final String SECRET_KEY = "5a300b56b760542ab15acd225fe8dabe88abeb678dfa3d4c9691d0983fde83f7";
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final HashService hashService;

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isValidAccess(String token, UserProfile user) {
        String userName = extractUsername(token);
        String type = extractType(token);

        return userName.equals(user.getUsername()) && !isTokenExpired(token) &&
                type.equals("access");
    }

    public boolean isValidRefresh(String token, String username) throws AuthenticationException {
        String userName = extractUsername(token);
        String type = extractType(token);

        Optional<RefreshToken> refreshTokenOptional = iRefreshTokenRepository.findByUsername(userName);

        if(refreshTokenOptional.isEmpty())
            throw new BadCredentialsException("User's refresh token was not in DB");

        return userName.equals(username) && type.equals("refresh")
            && hashService.hash(token).equals(refreshTokenOptional.get().getToken());
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAccessToken(UserProfile user) {
        // 15 mins
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("type", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15*60*1000)) // 15 mins
                .signWith(getSigninKey())
                .compact();
    }

    public String generateRefreshToken(UserProfile user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigninKey())
                .compact();
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

