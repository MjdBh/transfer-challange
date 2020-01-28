package com.revolut.challenge.service;


import com.revolut.challenge.BaseIT;
import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.repository.FinancialAccountRepository;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FinancialAccountServiceTest extends BaseIT {

    private final FinancialAccountService financialAccountServiceTest =
            new FinancialAccountService(new FinancialAccountRepository(dataContext));

    private String INVALID_ACCOUNT_NUMBER = "1234";
    private String CORRECT_ACCOUNT_NUMBER = "123";

    @Test
    public void shouldThrowIfAccountNumberNotExist() {
        assertThatThrownBy(() -> financialAccountServiceTest.getAccount(INVALID_ACCOUNT_NUMBER)).
                isInstanceOf(InvalidAccountException.class).hasMessage("Invalid Account number 1234");

    }

    @Test
    public void shouldNotCreateFinancialAccountWithoutStatus() {

        var financialAccountInvalid = FinancialAccount.builder().
                accountNumber("123").
                balance(BigDecimal.ZERO).
                currency(Currency.EUR).
                createDatetime(LocalDateTime.now()).
                build();
        assertThatThrownBy(() -> financialAccountServiceTest.createAccount(financialAccountInvalid))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    public void shouldCreateAccountSuccessfully() {

        var financialAccountInvalid = FinancialAccount.builder().
                accountNumber(CORRECT_ACCOUNT_NUMBER).
                balance(BigDecimal.ZERO).
                currency(Currency.EUR).
                createDatetime(LocalDateTime.now()).
                accountStatusType(AccountStatusType.ACTIVE).
                build();
        financialAccountServiceTest.createAccount(financialAccountInvalid);
        assertThat(financialAccountServiceTest.getAccount(CORRECT_ACCOUNT_NUMBER)).isNotNull();
    }
}
