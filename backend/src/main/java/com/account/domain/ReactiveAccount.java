package com.account.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * R2DBC용 리액티브 Account 엔티티
 * - 기존 JPA 엔티티와는 별도
 * @Entity 대신 @Table, @Id사용
 */
@Data
@Table("account") //실제 DB테이블명과 동일하게
public class ReactiveAccount {
    @Id
    private Long id;

    private String accountNumber;
    private Long userId;
    private Long balance;
}