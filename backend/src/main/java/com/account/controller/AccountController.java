package com.account.controller;

import com.account.domain.Account;
import com.account.domain.Transaction;
import com.account.dto.TransactionResponseDto;
import com.account.dto.TransferRequestDto;
import com.account.service.AccountService;
import com.security.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AccountController
 * -계좌 생성 및 내 계좌 목록 조회를 처리하는 API
 * -사용자 인증은 @AuthenticationPrincipal을 통해 처리
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 계좌 생성 API
     * [POST] /api/accounts
     * @param user 인증된 사용자(AccessToken기반)
     * @param accountNumber 생성할 계좌번호
     */
    @PostMapping
    public ResponseEntity<Account> createAccount (@AuthenticationPrincipal CustomUser user, @RequestParam String accountNumber) {
        Account created = accountService.createAccount(user.getUserId(), accountNumber);

        return ResponseEntity.ok(created);
    }

    /**
     * 내 계좌 전체 조회 API
     * [GET] /api/accounts/me?userId=1
     * @param user 인증된 사용자
     */
    @GetMapping("/me")
    public ResponseEntity<List<Account>> getMyAccounts(@AuthenticationPrincipal CustomUser user) {
        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());

        return ResponseEntity.ok(accounts);
    }

    /**
     * 계좌 송금
     * @param dto 송금 요청 정보
     */
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto dto) {
        accountService.transfer(dto);

        return ResponseEntity.ok().build();
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