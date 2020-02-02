package com.revolut.challenge.service;

import com.revolut.challenge.exception.DuplicateTransactionException;
import com.revolut.challenge.exception.InsufficientBalanceException;
import com.revolut.challenge.model.AccountTransaction;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.TransactionType;
import com.revolut.challenge.repository.AccountTransactionRepository;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.web.dto.AccountTransactionDTO;
import com.revolut.challenge.web.dto.AccountTransactionDetails;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AccountTransactionService {
    private final AccountTransactionRepository accountTransactionRepository;
    private final FinancialAccountRepository accountRepository;
    private final DSLContext dataContext;

    public AccountTransactionService(AccountTransactionRepository accountTransactionRepository,
                                     FinancialAccountRepository accountRepository,
                                     DSLContext dataContext) {
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountRepository = accountRepository;
        this.dataContext = dataContext;
    }

    /**
     * Make withdraw with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to withdraw
     * @param transactionId transaction Id of transaction for make idempotent
     * @param transfer      transfer Id related to transaction
     */
    public void createWithdrawTransaction(DSLContext dslContext, String accountNumber, BigDecimal amount, String transactionId, Long transfer) {
        validateDuplicateTransaction(transactionId, TransactionType.WITHDRAW);

        dslContext.transaction(dataConfiguration -> {
            var transactionCtx = DSL.using(dataConfiguration);

            //Lock account record to make concurrency safe!
            var financialAccount = accountRepository.lockFinancialAccount(transactionCtx, accountNumber);

            BigDecimal currentBalance = financialAccount.getBalance();

            if (amount.abs().compareTo(currentBalance) > 0) {
                throw new InsufficientBalanceException();
            }

            BigDecimal newBalance = currentBalance.subtract(amount);
            accountTransactionRepository.save(AccountTransaction.builder()
                    .transactionType(TransactionType.WITHDRAW)
                    .financialAccount(financialAccount.getId())
                    .balance(newBalance)
                    .amount(amount)
                    .createDatetime(LocalDateTime.now())
                    .transfer(transfer)
                    .transactionId(transactionId)
                    .build());

            accountRepository.updateBalance(transactionCtx, accountNumber, newBalance);

            log.info("Create withdraw from account {} with amount {} successfully.", accountNumber, amount);
        });
    }

    /**
     * Make withdraw with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to withdraw
     * @param transactionId transaction Id of transaction for make idempotent
     * @param transfer      transfer Id related to transaction
     */
    public void createWithdrawTransaction(String accountNumber, BigDecimal amount, String transactionId, Long transfer) {
        createWithdrawTransaction(dataContext, accountNumber, amount, transactionId, transfer);
    }

    /**
     * Make withdraw with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to withdraw
     * @param transactionId transaction Id of transaction for make idempotent
     */
    public void createWithdrawTransaction(String accountNumber, BigDecimal amount, String transactionId) {
        createWithdrawTransaction(accountNumber, amount, transactionId, null);
    }

    /**
     * Make deposit with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to deposit
     * @param transactionId transaction Id of transaction for make idempotent
     * @param transfer      transfer Id related to transaction
     */
    public void createDepositTransaction(DSLContext dslContext, String accountNumber, BigDecimal amount, String transactionId, Long transfer) {
        validateDuplicateTransaction(transactionId, TransactionType.DEPOSIT);

        dslContext.transaction(dataConfiguration -> {
            var transactionCtx = DSL.using(dataConfiguration);

            //Lock account record to make concurrency safe!
            var financialAccount = accountRepository.lockFinancialAccount(transactionCtx, accountNumber);

            BigDecimal currentBalance = financialAccount.getBalance();

            BigDecimal newBalance = currentBalance.add(amount);
            accountTransactionRepository.save(AccountTransaction.builder()
                    .transactionType(TransactionType.DEPOSIT)
                    .financialAccount(financialAccount.getId())
                    .balance(newBalance)
                    .amount(amount)
                    .createDatetime(LocalDateTime.now())
                    .transactionId(transactionId)
                    .transfer(transfer)
                    .build());

            accountRepository.updateBalance(transactionCtx, accountNumber, newBalance);

            log.info("Create deposit to account {} with amount {} successfully.", accountNumber, amount);
        });
    }

    /**
     * Make deposit with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to deposit
     * @param transactionId transaction Id of transaction for make idempotent
     * @param transfer      transfer Id related to transaction
     */
    public void createDepositTransaction(String accountNumber, BigDecimal amount, String transactionId, Long transfer) {
        createDepositTransaction(dataContext, accountNumber, amount, transactionId, transfer);
    }

    /**
     * Make deposit with given amount for input account
     *
     * @param accountNumber target number
     * @param amount        amount to deposit
     * @param transactionId transaction Id of transaction for make idempotent
     */
    public void createDepositTransaction(String accountNumber, BigDecimal amount, String transactionId) {
        createDepositTransaction(accountNumber, amount, transactionId, null);
    }

    /**
     * transaction id and type must be unique
     *
     * @param transactionId transaction Id
     */
    private void validateDuplicateTransaction(String transactionId, TransactionType transactionType) {
        if (accountTransactionRepository.isTransactionPresent(transactionId, transactionType)) {
            throw new DuplicateTransactionException();
        }
    }

    /**
     * get All transaction of given account number
     *
     * @param accountNumber transaction number
     * @return transaction list
     */
    public AccountTransactionDetails getAccountTransActionList(String accountNumber) {
        FinancialAccount byAccountNumber = accountRepository.getByAccountNumber(accountNumber);
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.getAccountTransactionList(byAccountNumber.getId());
        return new AccountTransactionDetails(
                accountTransactionList
                        .stream()
                        .map(accountTransaction -> AccountTransactionDTO.builder()
                                .amount(accountTransaction.getAmount())
                                .balance(accountTransaction.getBalance())
                                .createDatetime(accountTransaction.getCreateDatetime())
                                .financialAccount(accountTransaction.getFinancialAccount())
                                .transactionId(accountTransaction.getTransactionId())
                                .transactionType(accountTransaction.getTransactionType())
                                .transfer(accountTransaction.getTransfer())
                                .build())
                        .collect(Collectors.toList()));
    }
}
