package com.revolut.challenge.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionDetails {
    private List<AccountTransactionDTO> accountTransactionDTOList;
}
