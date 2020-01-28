package com.revolut.challenge.web.dto;

import com.revolut.challenge.model.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountResponseDTO implements Serializable {
    private String accountNumber;
    private Currency currency;
}

