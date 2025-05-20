package com.account.dto;

import com.account.domain.TransactionType;
import lombok.Getter;
import lombok.Setter;

/**
 * Kafka를 통해 거래내역 전송/수신 시 사용되는 DTO
 */
@Getter
@Setter
public class TransactionEventDto {
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private TransactionType type;
}