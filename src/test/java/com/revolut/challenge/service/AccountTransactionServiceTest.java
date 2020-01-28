package com.revolut.challenge.service;

import com.revolut.challenge.BaseIT;
import com.revolut.challenge.exception.DuplicateTransactionException;
import com.revolut.challenge.exception.InsufficientBalanceException;
import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.model.enumeration.TransactionType;
import com.revolut.challenge.repository.AccountTransactionRepository;
import com.revolut.challenge.repository.FinancialAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class AccountTransactionServiceTest extends BaseIT {

    private static final FinancialAccountRepository financialAccountRepositoryTest = new FinancialAccountRepository(dataContext);
    private static final AccountTransactionRepository accountTransactionRepositoryTest = new AccountTransactionRepository(dataContext);

    private static final AccountTransactionService accountTransactionServiceTest = new AccountTransactionService(accountTransactionRepositoryTest, financialAccountRepositoryTest, dataContext);

    @BeforeAll
    public static void prepareData() {
        financialAccountRepositoryTest.save(FinancialAccount.builder()
                .accountNumber("0000")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("500"))
                .currency(Currency.USD)
                .build());

        financialAccountRepositoryTest.save(FinancialAccount.builder()
                .accountNumber("1111")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("1000"))
                .currency(Currency.USD)
                .build());
    }

    @Test
    public void shouldCreateDepositForGivenAccountSuccessfully() {
        String accountNumber = "0000";
        String transactionId = UUID.randomUUID().toString();
        financialAccountRepositoryTest.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        accountTransactionServiceTest.createDepositTransaction(accountNumber, new BigDecimal(100), transactionId);

        assertThat(financialAccountRepositoryTest.getByAccountNumber(accountNumber).getBalance()).isEqualTo(new BigDecimal("600.00"));
    }

    @Test
    public void shouldThrowDuplicatedTransactionIfDepositTransactionIdBeDuplicated() {
        String accountNumber = "0000";
        String transactionId = UUID.randomUUID().toString();

        accountTransactionServiceTest.createDepositTransaction(accountNumber, new BigDecimal(100), transactionId);

        assertThatThrownBy(() ->
                accountTransactionServiceTest.createDepositTransaction(accountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(DuplicateTransactionException.class)
                .hasMessage("Transaction id is duplicated");

    }

    @Test
    public void shouldThrowInvalidAccountIfGivenAccountWasInvalidForDeposit() {
        String accountNumber = "INVALID_ACCOUNT";
        String transactionId = UUID.randomUUID().toString();

        assertThatThrownBy(() ->
                accountTransactionServiceTest.createDepositTransaction(accountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("Invalid Account number INVALID_ACCOUNT");
    }

    @Test
    public void shouldCreateWithdrawForGivenAccountSuccessfully() {
        String accountNumber = "0000";
        String transactionId = UUID.randomUUID().toString();

        financialAccountRepositoryTest.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        accountTransactionServiceTest.createWithdrawTransaction(accountNumber, new BigDecimal(100), transactionId);

        assertThat(financialAccountRepositoryTest.getByAccountNumber(accountNumber).getBalance()).isEqualTo(new BigDecimal("400.00"));
        assertThat(accountTransactionRepositoryTest.isTransactionPresent(transactionId, TransactionType.WITHDRAW)).isTrue();
    }

    @Test
    public void shouldThrowInsufficientBalanceIfAmountBeLowerThanAccountBalance() {
        String accountNumber = "0000";
        String transactionId = UUID.randomUUID().toString();

        financialAccountRepositoryTest.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        assertThatThrownBy(() -> accountTransactionServiceTest.createWithdrawTransaction(accountNumber, new BigDecimal(700), transactionId))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Balance is insufficient");

    }

    @Test
    public void shouldThrowInvalidAccountIfGivenAccountWasInvalidForWithDraw() {
        String accountNumber = "INVALID_ACCOUNT";
        String transactionId = UUID.randomUUID().toString();

        assertThatThrownBy(() ->
                accountTransactionServiceTest.createWithdrawTransaction(accountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("Invalid Account number INVALID_ACCOUNT");
    }

    @Test
    public void shouldThrowDuplicatedTransactionIfWithdrawTransactionIdBeDuplicated() {
        String accountNumber = "0000";
        String transactionId = UUID.randomUUID().toString();

        accountTransactionServiceTest.createWithdrawTransaction(accountNumber, new BigDecimal(100), transactionId);

        assertThatThrownBy(() ->
                accountTransactionServiceTest.createWithdrawTransaction(accountNumber, new BigDecimal(100), transactionId))
                .isInstanceOf(DuplicateTransactionException.class)
                .hasMessage("Transaction id is duplicated");
    }

    @Test
    public void shouldThrowIfAccountNumberNotExist() {
        String accountNumber = "INVALID_ACCOUNT";
        assertThatThrownBy(() -> accountTransactionServiceTest.getAccountTransActionList(accountNumber)).
                isInstanceOf(InvalidAccountException.class).hasMessage("Invalid Account number INVALID_ACCOUNT");

    }

}
