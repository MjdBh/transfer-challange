package com.revolut.challenge.service;

import com.revolut.challenge.BaseIT;
import com.revolut.challenge.exception.IncompatibleCurrencyException;
import com.revolut.challenge.exception.InsufficientBalanceException;
import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.exception.SameAccountException;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.model.enumeration.TransferStatusType;
import com.revolut.challenge.repository.AccountTransactionRepository;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.repository.TransferRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TransferServiceTest extends BaseIT {

    private static final FinancialAccountRepository financialAccountRepositoryTest = new FinancialAccountRepository(dataContext);
    private static final AccountTransactionRepository accountTransactionRepositoryTest = new AccountTransactionRepository(dataContext);
    private static final TransferRepository transferRepository = new TransferRepository(dataContext);

    private static final AccountTransactionService accountTransactionServiceTest = new AccountTransactionService(accountTransactionRepositoryTest, financialAccountRepositoryTest, dataContext);
    private static final TransferService transferServiceTest = new TransferService(financialAccountRepositoryTest, transferRepository, accountTransactionServiceTest);

    @BeforeAll
    public static void prepareData() {
        financialAccountRepositoryTest.save(FinancialAccount.builder()
                .accountNumber("2222")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("500"))
                .currency(Currency.USD)
                .build());

        financialAccountRepositoryTest.save(FinancialAccount.builder()
                .accountNumber("3333")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("1000"))
                .currency(Currency.USD)
                .build());
    }

    @Test
    public void shouldTransferMoneySuccessfully() {
        String fromAccountNumber = "2222";
        String toAccountNumber = "3333";

        financialAccountRepositoryTest.updateBalance(dataContext, fromAccountNumber, new BigDecimal("500"));
        financialAccountRepositoryTest.updateCurrency(dataContext, fromAccountNumber, Currency.USD);
        financialAccountRepositoryTest.updateBalance(dataContext, toAccountNumber, new BigDecimal("1000"));
        financialAccountRepositoryTest.updateCurrency(dataContext, toAccountNumber, Currency.USD);

        transferServiceTest.createTransfer(fromAccountNumber, toAccountNumber, new BigDecimal(100L), UUID.randomUUID().toString());

        FinancialAccount fromAccount = financialAccountRepositoryTest.getByAccountNumber(fromAccountNumber);
        FinancialAccount toAccount = financialAccountRepositoryTest.getByAccountNumber(toAccountNumber);

        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("400.00"));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("1100.00"));

        assertThat(transferRepository.getTransferByAccounts(fromAccount.getId(), toAccount.getId(), TransferStatusType.DONE).isPresent());
    }

    @Test
    public void shouldThrowSameAccountIfTwoAccountBeSame() {
        String accountNumber = "2222";
        assertThatThrownBy(() ->
                transferServiceTest.createTransfer(accountNumber, accountNumber, new BigDecimal(100L), UUID.randomUUID().toString()))
                .isInstanceOf(SameAccountException.class)
                .hasMessage("Transfer is invalid for same account");
    }

    @Test
    public void shouldThrowInvalidAccountIfGivenFromAccountWasInvalidForTransfer() {
        String fromAccountNumber = "INVALID_FROM_ACCOUNT";
        String toAccountNumber = "2222";
        String transactionId = UUID.randomUUID().toString();

        assertThatThrownBy(() ->
                transferServiceTest.createTransfer(fromAccountNumber, toAccountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("Invalid Account number INVALID_FROM_ACCOUNT");
    }

    @Test
    public void shouldThrowInvalidAccountIfGivenToAccountWasInvalidForTransfer() {
        String fromAccountNumber = "2222";
        String toAccountNumber = "INVALID_To_ACCOUNT";
        String transactionId = UUID.randomUUID().toString();

        assertThatThrownBy(() ->
                transferServiceTest.createTransfer(fromAccountNumber, toAccountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("Invalid Account number INVALID_To_ACCOUNT");
    }

    @Test
    public void shouldThrowInsufficientBalanceIfAmountBiggerThanFromAccountBalance() {

        String fromAccountNumber = "2222";
        String toAccountNumber = "3333";

        financialAccountRepositoryTest.updateBalance(dataContext, fromAccountNumber, new BigDecimal("1000"));
        financialAccountRepositoryTest.updateBalance(dataContext, toAccountNumber, new BigDecimal("1000"));

        assertThatThrownBy(() ->
                transferServiceTest.createTransfer(fromAccountNumber, toAccountNumber, new BigDecimal(60000), UUID.randomUUID().toString()))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Balance is insufficient");

    }

    @Test
    public void shouldThrowIncompatibleCurrencyIfAccountCurrencyIsIncompatible() {
        String fromAccountNumber = "2222";
        String toAccountNumber = "3333";

        financialAccountRepositoryTest.updateCurrency(dataContext, fromAccountNumber, Currency.USD);
        financialAccountRepositoryTest.updateCurrency(dataContext, toAccountNumber, Currency.EUR);

        assertThatThrownBy(() ->
                transferServiceTest.createTransfer(fromAccountNumber, toAccountNumber, new BigDecimal(100), UUID.randomUUID().toString()))
                .isInstanceOf(IncompatibleCurrencyException.class)
                .hasMessage("Currency of accounts is incompatible");
    }
}
