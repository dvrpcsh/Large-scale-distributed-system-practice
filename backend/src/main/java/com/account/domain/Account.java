package com.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Account (계좌) 엔티티
 * - 하나의 사용자가 여러 개의 계좌를 가질 수 있음
 * - 계좌번호, 사용자 ID, 잔액, 생성일 등을 관리
 */
@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 계좌 PK

    @Column(nullable = false, unique = true)
    private String accountNumber;  // 계좌번호 (중복 불가)

    @Column(nullable = false)
    private Long userId;  // 계좌 소유자 (사용자 ID)

    @Column(nullable = false)
    private Long balance;  // 계좌 잔액 (원 단위)

    private LocalDateTime createdAt;  // 생성일

    /**
     * 계좌 생성자
     * @param accountNumber 계좌번호
     * @param userId 사용자 ID
     */
    public Account(String accountNumber, Long userId) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = 0L;  // 초기 잔액은 0원
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 💸 입금 처리 메서드
     * @param amount 입금 금액
     */
    public void deposit(Long amount) {
        this.balance += amount;
    }

    /**
     * 💸 출금 처리 메서드
     * @param amount 출금 금액
     * @throws IllegalArgumentException 잔액 부족 시 예외 발생
     */
    public void withdraw(Long amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }
}
