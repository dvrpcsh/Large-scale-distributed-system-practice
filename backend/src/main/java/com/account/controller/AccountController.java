package com.account.controller;

import com.account.domain.Account;
import com.account.domain.Transaction;
import com.account.dto.TransactionResponseDto;
import com.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AccountController
 * -계좌 생성 및 내 계좌 목록 조회를 처리하는 API
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 계좌 생성 API
     * [POST] /api/accounts
     * @param userId 사용자ID(임시로 파라미터로 받음)
     * @param accountNumber 생성할 계좌번호
     */
    @PostMapping
    public ResponseEntity<Account> createAccount (@RequestParam Long userId, @RequestParam String accountNumber) {
        Account created = accountService.createAccount(userId, accountNumber);

        return ResponseEntity.ok(created);
    }

    /**
     * 내 계좌 전체 조회 API
     * [GET] /api/accounts/me?userId=1
     * 추후 JWT인증 적용 시 토큰에서 userId 추출 방식으로 변경 가능
     */
    @GetMapping("/me")
    public ResponseEntity<List<Account>> getMyAccounts(@RequestParam Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);

        return ResponseEntity.ok(accounts);
    }

    /**
     * 거래내역 조회 API
     * -요청된 계좌번호 기준으로 모든 입출금/송금 내역 조회
     * -송신자/수신자 모두 포함된 거래를 반환
     *
     * @param accountNumber 요청 파라미터로 전달되는 계좌번호
     * @return 거래내역 목록(JSON배열)
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(@RequestParam String accountNumber) {
        List<TransactionResponseDto> list = accountService.getTransactionHistory(accountNumber);

        return ResponseEntity.ok(list);
    }

}