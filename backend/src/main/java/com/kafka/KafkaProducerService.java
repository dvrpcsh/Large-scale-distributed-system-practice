package com.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer Service
 * - 거래내역을 Kafka로 전송
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 거래내역 전송
     * @param topic Kafka 토픽
     * @param message 직렬화된 메시지(JSON)
     */
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}