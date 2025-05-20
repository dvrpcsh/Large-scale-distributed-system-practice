package com.account.repository;

import com.account.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * R2DBC 기반 계좌 레포지토리
 * - Spring Data R2DBC의 ReactiveCrudRepository 상속
 * - Flux<T>로 여러 행 비동기 처리
 */
public interface ReactiveAccountRepository extends ReactiveCrudRepository<Account, Long> {

    /**
     * 사용자 ID로 계좌 목록 조회(비동기 반환)
     * @param userId
     * @return Flux<Account> (Non-Blocking방식)
     */
    Flux<Account> findByUserId(Long userId);
}