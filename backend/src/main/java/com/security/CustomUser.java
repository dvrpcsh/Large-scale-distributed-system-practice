package com.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUser implements UserDetails {
    private Long userId; //실제 사용자 ID
    private String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return null;//권한은 사용하지 않음
    }

    @Override
    public String getPassword() {
        return null; // 인증은 JWT로 처리하므로 필요 없음
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

}