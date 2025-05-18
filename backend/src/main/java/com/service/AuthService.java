package com.service;

import com.dto.AuthRequestDto;
import com.dto.AuthResponseDto;
import com.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * AuthService
 *
 * -로그인, 로그아웃, 토큰 재발급 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Value("${jwt.expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidity;

    /**
     * 로그인 처리
     * @param requestDto 이메일 + 비밀번호
     * @return 액세스 토큰 + 리프레쉬 토큰
     */
    public AuthResponseDto login(AuthRequestDto requestDto) {
        //TODO: 실제 인증 로직(이메일/비밀번호 검증)은 생략. 성공했다고 가정

        String userId = requestDto.getEmail(); //여기서는 email을 ID대체

        //1. 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(userId);
        String refreshToken = jwtTokenProvider.generateToken(userId);

        //2.RefreshToken Redis에 저장
        redisService.saveRefreshToken(
                userId,
                refreshToken,
                Duration.ofMillis(refreshTokenValidity)
        );

        return new AuthResponseDto(accessToken, refreshToken);
    }

    /**
     * 로그아웃 처리
     *
     * @param accessToken 사용자 AccessToken
     */
    public void logout(String accessToken) {
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        //1.RefreshToken 삭제
        redisService.deleteRefreshToken(userId);

        //2.AccessToken 블랙리스트에 등록
        long exp = jwtTokenProvider.getRemainingExpiration(accessToken);
        redisService.addToBlacklist(accessToken, Duration.ofMillis(exp));
    }

    /**
     * 토큰 재발급 처리
     *
     * [데이터 흐름]
     * 1. 전달받은 RefreshToken의 유효성 검사 (JWT 구조, 서명, 만료 여부 등)
     * 2. 해당 RefreshToken에서 사용자 ID(userId)를 추출
     * 3. Redis에 저장된 RefreshToken을 조회
     * 4. 두 RefreshToken이 일치하는지 비교
     * 5. 일치하면 새로운 AccessToken을 발급
     */
    public String reissue(String refreshToken) {
        //1.전달받은 RefreshToken의 유효성 검사
        if(!jwtTokenProvider.validationToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        //2.RefreshToken에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        //3.Redis에 저장된 RefreshToken조회
        String stored = redisService.getRefreshToken(userId);

        //4.저장된 토큰과 전달받은 토큰 비교
        if(!refreshToken.equals(stored)) {
            throw new IllegalArgumentException("Token mismatch");
        }

        //5.새 AccessToken 발급 후 반환
        return jwtTokenProvider.generateToken(userId);
    }
}
