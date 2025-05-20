package com.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import com.security.JwtTokenProvider;

/**
 * JWT 인증 필터
 * -Spring Security 매 요청마다 작동하는 필터
 * - 인증된 사용자는 SecurityContextHolder에 등록됨
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [요청 흐름]
     * 1. 클라이언트가 Autorization: Bearer {accessToken} 형태의 헤더로 요청을 보냄
     * 2. 해당 메서드가 요청을 가로채서 토큰을 추출하고 유효성 검사를 수행
     * 3. 토큰이 유효하면 사용자의 ID를 추출하여 인증 객체(Authentication) 생성
     * 4. SecurityContextHolder에 인증 객체 저장 ->  이후 @AuthenticationPrincipal 등에서 사용자 정보 접근 가능
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 다음 필터로 연결
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        //1.Authorization 헤더에서 JWT 추출 ("Bearer ..." 형식)
        String header = request.getHeader("Authorization");
        String token = null;

        //2. "Bearer "제거
        if(header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        //3. 토큰이 존재하고 유효한 경우
        if(token != null && jwtTokenProvider.validationToken(token)) {
            // 3-1. 토큰에서 사용자 ID 추출
            String userId = jwtTokenProvider.getUserIdFromToken(token);

            // 3-2. 사용자 인증 객체 생성 (비밀번호 및 권한은 여기선 사용 안 함)
            CustomUser user = new CustomUser(Long.parseLong(userId), ""); //username은 비워둠
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // 3-3. 인증 객체에 요청 정보를 추가
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 3-4. SecurityContextHolder에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //4. 필터 체인 계속 실행 (다음 필터 또는 컨트롤러로 전달)
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/auth");
    }
}