package com.kafka;

import com.account.domain.Transaction;
import com.account.domain.TransactionType;
import com.account.dto.TransactionEventDto;
import com.account.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer Service
 * - Kafka에서 거래내역 이벤트를 수신하여 DB에 저장하는 역할
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    /**
     * KafkaListener
     * - "transaction-topic"이라는 토픽으로부터 메시지를 받음
     * - JSON -> Transaction DTO -> DB저장
     *
     * @param record Kafka 메시지(key/value/offset 등 포함)
     */
    @KafkaListener(topics = "transaction-topic", groupId = "transaction-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            // 1.메시지 내용 추출
            String message = record.value();
            log.info("Kafka수신: {}", message);

            // 2.JSON문자열 -> Transaction 객체로 변환
            TransactionEventDto dto = objectMapper.readValue(message, TransactionEventDto.class);

            // 3.Entity생성
            Transaction transaction = new Transaction(
                    dto.getFromAccountId(),
                    dto.getToAccountId(),
                    dto.getAmount(),
                    dto.getType()
            );

            // 4.DB저장
            transactionRepository.save(transaction);
            log.info("거래내역 저장 완료(ID from {} to {}", dto.getFromAccountId(), dto.getToAccountId());
        } catch(Exception e) {
            log.error("Kafka메시지 처리 실패: {}",e.getMessage(), e);
        }
    }
}