package com.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
/**
 * 흐름 요약표(좀 과한 것 같긴 하나 일단 작성)
 *
 * HTTP 요청
 *   ↓
 * JwtAuthenticationFilter
 *   ↓
 * JwtTokenProvider.validateToken() → OK
 *   ↓
 * String userId = getUserIdFromToken(token)
 *   ↓
 * new JwtUserAuthentication(userId, null, null)
 *   ↓
 * SecurityContextHolder.getContext().setAuthentication(auth)
 *
 * 이렇게 설정되면 컨트롤러에서 @AuthenticationPrincipal 또는
 * SecurityContextHolder.getContext().getAuthentication() 으로 userId를 꺼낼 수 있음
 */

/**
 * JwtUserAuthentication
 *
 * - 인증된 사용자 정보를 담는 Spring Security 인증 객체
 * - 필터(JwtAuthenticationFilter)에서 사용자를 인증한 후 SecurityContextHolder에 저장할 때 사용함
 * - UserDetailsService 없이 JWT만으로 인증 처리를 할 수 있게 해주는 클래스
 */
public class JwtUserAuthentication extends AbstractAuthenticationToken {

    //사용자ID
    private final String principal;

    /**
     * 인증된 사용자 정보를 담은 객체 생성
     *
     * @param principal 사용자 식별값 (userId)
     * @param credentials 비밀번호 등 (우리는 사용하지 않음)
     * @param authorities 사용자 권한 (우리는 기본 null로 설정)
     */
    public JwtUserAuthentication(String principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true); //인증된 사용자임을 표시
    }

    /**
     * 비밀번호 등 민감정보 변환(사용안함)
     */
    @Override
    public Object getCredentials() {

        return null;
    }

    /**
     * 사용자 ID (principal) 반환
     */
    @Override
    public Object getPrincipal() {

        return principal;
    }
}