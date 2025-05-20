package com.account.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * ✅ R2DBC용 거래 내역 엔티티
 * - 모든 송금, 입금, 출금 정보를 비동기로 기록
 * - Kafka 연동 전까지는 DB에 저장
 */
@Data
@Table("transactions") // 실제 테이블명과 일치시켜야 함
public class ReactiveTransaction {

    @Id
    private Long id; // 거래 ID (PK)

    private Long fromAccountId; // 송금 계좌 ID
    private Long toAccountId;   // 수신 계좌 ID
    private Long amount;        // 거래 금액
    private TransactionType type;       // 거래 타입 (TRANSFER, DEPOSIT, ...)
    private LocalDateTime createdAt = LocalDateTime.now(); // 거래 시간

    /**
     * 생성자
     * @param fromAccountId 송금 계좌
     * @param toAccountId 수신 계좌
     * @param amount 금액
     * @param type 거래 타입
     */
    public ReactiveTransaction(Long fromAccountId, Long toAccountId, Long amount, TransactionType type) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
}
