package com.account.controller;

import com.account.domain.Account;
import com.account.service.ReactiveAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 리액티브 계좌 컨트롤러(WebFlux 테스트용)
 *
 * 일반 컨트롤러: 요청 -> 스레드 대기 -> 응답
 * WebFlux컨트롤러: 요청 -> 등록만 하고 즉시 반환 -> 응답 준비 시 비동기 전달
 */
@RestController
@RequestMapping("/api/reactive/accounts")
@RequiredArgsConstructor
public class ReactiveAccountController {
    private final ReactiveAccountService reactiveAccountService;

    @GetMapping("/{userId}")
    public Flux<Account> getAccounts(@PathVariable Long userId) {

        return reactiveAccountService.getAccountsByUserId(userId);
    }
}