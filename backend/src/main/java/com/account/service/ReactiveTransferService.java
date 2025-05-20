package com.account.service;

import com.account.domain.ReactiveAccount;
import com.account.domain.ReactiveTransaction;
import com.account.domain.TransactionType;
import com.account.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 *  비동기 송금 트랜잭션 서비스
 * - 출금 → 입금 → 거래내역 저장을 하나의 R2DBC 트랜잭션으로 처리
 * - 각 단계에 낙관적 락 충돌 대비 예외처리 추가
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveTransferService {

    private final R2dbcEntityTemplate template;
    private final ReactiveTransactionManager transactionManager;

    /**
     * 송금 로직 (R2DBC 트랜잭션)
     * @param dto 송금 요청 DTO
     * @return Mono<Void>
     */
    public Mono<Void> transfer(TransferRequestDto dto) {
        // 트랜잭션 오퍼레이터 생성
        TransactionalOperator txOperator = TransactionalOperator.create(transactionManager);

        return template.select(ReactiveAccount.class)
                .matching(Query.query(where("account_number").is(dto.getFromAccountNumber())))
                .one()
                .zipWith(template.select(ReactiveAccount.class)
                        .matching(Query.query(where("account_number").is(dto.getToAccountNumber())))
                        .one())
                .flatMap(tuple -> {
                    ReactiveAccount from = tuple.getT1();
                    ReactiveAccount to = tuple.getT2();

                    // 1. 출금 / 입금 처리
                    from.setBalance(from.getBalance() - dto.getAmount());
                    to.setBalance(to.getBalance() + dto.getAmount());

                    // 2. 거래 내역 생성
                    ReactiveTransaction tx = new ReactiveTransaction(
                            from.getId(),
                            to.getId(),
                            dto.getAmount(),
                            TransactionType.TRANSFER
                    );

                    // 3. 트랜잭션 내에서 모든 작업 수행 + 예외 처리
                    return template.update(from)
                            .onErrorResume(e -> {
                                log.warn(" 출금 계좌 업데이트 실패: {}", e.getMessage());
                                return Mono.error(new IllegalStateException("출금 계좌 처리 중 충돌이 발생했습니다."));
                            })
                            .then(template.update(to)
                                    .onErrorResume(e -> {
                                        log.warn(" 입금 계좌 업데이트 실패: {}", e.getMessage());
                                        return Mono.error(new IllegalStateException("입금 계좌 처리 중 충돌이 발생했습니다."));
                                    }))
                            .then(template.insert(tx)
                                    .onErrorResume(e -> {
                                        log.warn(" 거래내역 저장 실패: {}", e.getMessage());
                                        return Mono.error(new IllegalStateException("거래내역 저장 중 오류가 발생했습니다."));
                                    }));
                })
                .as(txOperator::transactional) // 전체를 트랜잭션으로 묶음
                .then();
    }
}
