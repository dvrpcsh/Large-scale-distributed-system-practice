package com.account.dto;

import com.account.domain.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import javax.sound.midi.Track;
import java.time.LocalDateTime;

/**
 * 1. 사용자가 계좌번호를 입력하면
 * 2. 해당 계좌가 송금자든 수신자든
 * 3. 관련된 모든 거래내역을 조회해서
 * 4. 프론트에 DTO 형태로 반환
 *
 * 거래내역 응답 DTO
 * -클라이언트에게 반환할 거래 상세 정보
 */
@Getter
@Setter
public class TransactionResponseDto {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}