package com.account.service;

import com.account.domain.Account;
import com.account.repository.ReactiveAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 리액티브 계좌 서비스(WebFlux)
 */
@Service
@RequiredArgsConstructor
public class ReactiveAccountService {

    private final ReactiveAccountRepository reactiveAccountRepository;

    /**
     * 사용자ID로 계좌목록 조회(Non-Blocking 방식)
     */
    public Flux<Account> getAccountsByUserId(Long userId) {

        return reactiveAccountRepository.findByUserId(userId);
    }
}