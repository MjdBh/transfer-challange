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
public class CreateDepositRequestDTO implements Serializable {
    private String transactionId;

    @NotBlank(message = "Account number cant be null")
    private String accountNumber;
    @NotNull(message = "Amount cant be null")
    @Positive(message = "Amount must be greater than 0")
    private Long amount;

}
