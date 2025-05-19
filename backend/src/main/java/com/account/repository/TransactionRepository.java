package com.account.repository;

import com.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TransactionRepository
 * - 거래내역에 대한 기본 CRUD 제공
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * 송금/수신 계좌 기준으로 거래내역 조회
     * @param accountId 계좌 ID
     * @return 거래 리스트(입금/출금/이체)
     */
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);

}