package com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * RedisService
 *
 * -Redis를 활용하여 인증 관련 토큰을 관리하는 서비스
 * -RefreshToken 저장 및 조회
 * -AccessToken 블랙리스트 등록 및 검증 처리
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * RefreshToken 저장
     * @param userid 사용자 ID
     * @param refreshToken 발급된 리프레쉬 토큰
     * @param expiration 만료 시간(Duration)
     */
    public void saveRefreshToken(String userId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue()
                .set(REFRESH_PREFIX + userId, refreshToken, expiration);
    }

    /**
     * RefreshToken 조회
     * @param userId 사용자 ID
     * @return 저장된 refresh Token(없으면 null반환)
     */
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
    }

    /**
     * RefreshToken 삭제(로그아웃 또는 재발급)
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }

    /**
     * AccessToken 블랙리스트에 등록
     * @param accessToken 토큰 문자열
     * @param expiration 만료 시간(Duration)
     */
    public void addToBlacklist(String accessToken, Duration expiration) {
        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + accessToken, "blacklisted", expiration);
    }

    /**
     * 블랙리스트 등록 여부 확인
     * @param accessToken 토큰 문자열
     * @param true = 블랙리스트에 있음 -> 인증 거부
     */
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }
}