package com.revolut.challenge.web.dto;

import com.revolut.challenge.model.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequestDTO implements Serializable {
    @NotNull( message = "Currency of account is required.")
    private Currency currency;
}
