package com.controller;

import com.dto.AuthRequestDto;
import com.dto.AuthResponseDto;
import com.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * - 사용자 인증 관련 API (로그인, 로그아웃, 토큰 재발급) 엔드포인트 제공
 * - 실제 비즈니스 로직은 AuthService에서 처리하고,
 *   이 컨트롤러는 HTTP 요청/응답을 담당함
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * [기능]
     * - 현재 사용자의 AccessToken을 블랙리스트에 등록하고,
     *   Redis에서 RefreshToken을 삭제함
     *
     * [요청 헤더]
     * - Authorization: Bearer {accessToken}
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);//"Bearer 제거"
        authService.logout(token);

        return ResponseEntity.ok().build();
    }

    /**
     * [기능]
     * -RefreshToken을 검증하고 새로운 AccessToken발급
     *
     */
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@RequestHeader("Refresh-Token") String refreshToken) {

        return ResponseEntity.ok(authService.reissue(refreshToken));
    }
}
