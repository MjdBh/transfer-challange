package com.revolut.challenge.model;


import com.revolut.challenge.model.enumeration.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class FinancialAccount  implements Serializable {
    private Long id;
    private AccountStatusType accountStatusType;
    private String accountNumber;
    private LocalDateTime createDatetime;
    private BigDecimal balance;
    private Currency currency;
}
