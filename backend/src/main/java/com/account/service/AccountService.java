package com.account.service;

import com.account.domain.Account;
import com.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AccountService
 * - 계좌 생성, 조회 등의 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    /**
     * 계좌 생성
     * @param userId 사용자 ID
     * @param accountNumber 생성할 계좌번호 (고유)
     * @return 생성된 Account객체
     */
    @Transactional
    public Account createAccount(Long userId, String accountNumber) {
        Account newAccount = new Account(accountNumber, userId);

        return accountRepository.save(newAccount);
    }

    /**
     * 사용자 ID로 계좌 전체 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 계좌 리스트
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {

        return accountRepository.fintByUserId(userId);
    }

    /**
     * 계좌번호로 계좌 단건 조회
     * @param accountNumber 계좌번호
     * @return Account(없으면 null 또는 예외처리 가능)
     */
    @Transactional(readOnly = true)
    public Account getByAccountNumber(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber);
    }
}