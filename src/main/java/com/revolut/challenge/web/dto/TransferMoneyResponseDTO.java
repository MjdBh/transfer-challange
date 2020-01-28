package com.revolut.challenge.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyResponseDTO implements Serializable {
    private String transactionId;
}
