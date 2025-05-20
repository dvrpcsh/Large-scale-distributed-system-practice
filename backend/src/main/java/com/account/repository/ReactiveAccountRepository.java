package com.account.repository;

import com.account.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReactiveAccountRepository extends ReactiveCrudRepository<Account, Long> {
    Flux<Account> findByUserId(Long userId);
}