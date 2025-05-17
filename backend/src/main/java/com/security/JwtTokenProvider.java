package com.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.Signature;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 * -AccessToken 생성
 * -토큰에서 사용자 정보 추출
 * -토큰 유효성 검사
 */
@Component
public class JwtTokenProvider {

    // application.yml에서 주입 받을 값
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * JWT생성
     * @param userId 사용자 ID(setSubject로 저장)
     * @param JWT문자열
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 사용자 ID 추출
     * @param token JWT
     * @param userId
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);

        return claims.getSubject();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validationToken(String token) {
        try {
            parseClaims(token);

            return true;
        } catch(JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Claims 파싱
     */
    private Claims parseClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
