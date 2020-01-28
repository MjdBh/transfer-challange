package com.revolut.challenge.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequestDTO implements Serializable {

    private String transactionId;
    @NotBlank(message = "From account number can not be null")
    private String fromAccount;
    @NotBlank(message = "To account number can not be null")
    private String toAccount;
    @NotNull(message = "Amount can not be null")
    @Positive(message = "Amount must be greater than 0")
    private Long amount;

}
