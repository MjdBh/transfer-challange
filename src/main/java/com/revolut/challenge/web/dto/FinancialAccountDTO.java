package com.revolut.challenge.web.dto;


import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAccountDTO implements Serializable {

    private AccountStatusType accountStatusType;
    private String accountNumber;
    private LocalDateTime createDatetime;
    private BigDecimal balance;
    private Currency currency;
}
