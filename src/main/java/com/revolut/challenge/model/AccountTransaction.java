package com.revolut.challenge.model;


import com.revolut.challenge.model.enumeration.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountTransaction implements Serializable {
    private Long id;
    private LocalDateTime createDatetime;
    private Long financialAccount;
    private BigDecimal amount;
    private BigDecimal balance;
    private TransactionType transactionType;
    private Long transfer;
    private String transactionId;
}
