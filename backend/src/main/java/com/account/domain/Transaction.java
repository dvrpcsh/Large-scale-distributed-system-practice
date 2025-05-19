package com.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.account.domain.TransactionType;

import java.time.LocalDateTime;

/**
 * Transaction(거래 내역) 엔티티
 * -모든 송금, 입금, 출금에 대해 거래 정보를 기록함
 * -Kafka 비동기 처리 전까지는 DB 기반으로 저장
 */
@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //거래 ID(PK)

    @Column(nullable = false)
    private Long fromAccountId; //송금 계좌 ID

    @Column(nullable = false)
    private Long toAccountId; //수신 계좌 ID

    @Column(nullable = false)
    private Long amount; // 거래 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // 거래 타입(입금, 출금, 이체 등)

    private LocalDateTime createdAt; //거래 시각

    /**
     * 거래내역 생성자
     * @param fromAccountId 송금자 계좌 ID
     * @param toAccountId 수신자 계좌 ID
     * @param amount 거래 금액
     * @param transactionType 거래 타입(TRANSFER, DEPOSIT, WITHDRAW)
     */
    public Transaction(Long fromAccountId, Long toAccountId, Long amount, TransactionType transactionType) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = transactionType;
        this.createdAt = LocalDateTime.now();
    }
}