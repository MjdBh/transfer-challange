package com.revolut.challenge.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revolut.challenge.model.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransactionDTO {
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime createDatetime;
    private Long financialAccount;
    private BigDecimal amount;
    private BigDecimal balance;
    private TransactionType transactionType;
    private Long transfer;
    private String transactionId;
}
