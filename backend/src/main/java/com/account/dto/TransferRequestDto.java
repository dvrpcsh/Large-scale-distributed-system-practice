package com.account.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 송금 요청 DTO
 * - 송금 시 필요한 정보 전달용 객체
 */
@Getter
@Setter
public class TransferRequestDto {
    private String fromAccountNumber;
    private String toAccountNumber;
    private Long amount;
}