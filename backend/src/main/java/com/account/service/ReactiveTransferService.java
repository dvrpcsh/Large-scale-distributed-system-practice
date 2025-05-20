package com.account.service;

import com.account.domain.ReactiveAccount;
import com.account.domain.ReactiveTransaction;
import com.account.domain.TransactionType;
import com.account.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 * 💸 비동기 송금 트랜잭션 서비스
 * - 출금 → 입금 → 거래내역 저장을 하나의 R2DBC 트랜잭션으로 처리
 */
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

                    // 3. 모든 작업을 트랜잭션으로 묶어 실행
                    return template.update(from)
                            .then(template.update(to))
                            .then(template.insert(tx));
                })
                .as(txOperator::transactional)
                .then();
    }
}
