package com.account.service;

import com.account.domain.Account;
import com.account.domain.TransactionType;
import com.account.dto.TransactionResponseDto;
import com.account.dto.TransferRequestDto;
import com.account.repository.AccountRepository;
import com.account.domain.Transaction;
import com.account.repository.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

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

        return accountRepository.findByUserId(userId);
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

    /**
     * 1. 송금 요청이 들어오면
     * 2. 보내는 사람 계좌 잔액 확인
     * 3. 출금 → 수신 계좌에 입금 처리
     * 4. 거래내역(Transaction) 저장
     *
     * 계좌 간 송금
     * @param dto 송금 요청 DTO
     */
    @Transactional
    public void transfer(TransferRequestDto dto) {
        //1.계좌 조회
        Account from = accountRepository.findByAccountNumber(dto.getFromAccountNumber());
        Account to = accountRepository.findByAccountNumber(dto.getToAccountNumber());

        //2.존재 여부 확인
        if(from == null || to == null) {
            throw new IllegalArgumentException("계좌가 존재하지 않습니다.");
        }

        //3.출금 및 입금 처리
        from.withdraw(dto.getAmount());
        to.deposit(dto.getAmount());

        //4.거래내역 저장
        Transaction transaction = new Transaction(
                from.getId(), to.getId(), dto.getAmount(), TransactionType.TRANSFER);
        transactionRepository.save(transaction);
    }

    /**
     * 사용자의 계좌번호를 기준으로 해당 계좌의 모든 거래내역을 조회
     * @param accountNumber 조회할 계좌번호
     * @return 거래내역 리스트(해당 계좌가 송신자든 수신자든 포함)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionHistory(String accountNumber) {
        //1.계좌번호로 계좌 조회
        Account account = accountRepository.findByAccountNumber(accountNumber);

        //2.계좌 존재 여부 확인(없을 경우 예외 발생)
        if(account == null) {
            throw new IllegalArgumentException("해당 계좌가 존재하지 않습니다.");
        }

        //3.거래내역 조회(송신자 또는 수신자로 포함된 거래들 모두)
        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(account.getId(), account.getId());

        //4.Entity -> DTO 변환(클라이언트에게 불필요한 정보 제거)
        return transactions.stream().map(t -> {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setId(t.getId());
            dto.setFromAccountId(t.getFromAccountId());
            dto.setToAccountId(t.getToAccountId());
            dto.setAmount(t.getAmount());
            dto.setType(t.getType());
            dto.setCreatedAt(t.getCreatedAt());

            return dto;
        }).toList();
    }
}