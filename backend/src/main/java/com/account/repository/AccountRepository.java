package com.account.repository;

import com.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * AccountRepository
 * - 계좌 테이블에 대한 기본 CRUD 제공
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 사용자ID로 계좌 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 모든 계좌 리스트
     */
    List<Account> findByUserId(Long userId);

    /**
     * 계좌번호로 계좌 조회(송금 등에서 사용)
     */
    Account findByAccountNumber(String accountNumber);

}