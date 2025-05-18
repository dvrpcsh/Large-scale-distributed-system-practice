package com.dto;

import lombok.Getter;

/**
 * 로그인 요청 DTO
 */
@Getter
public class AuthRequestDto {
    private String email;
    private String password;
}