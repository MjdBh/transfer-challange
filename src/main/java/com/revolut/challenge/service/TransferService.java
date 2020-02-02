package com.revolut.challenge.service;

import com.revolut.challenge.exception.IncompatibleCurrencyException;
import com.revolut.challenge.exception.InsufficientBalanceException;
import com.revolut.challenge.exception.SameAccountException;
import com.revolut.challenge.jooq.tables.records.TransferRecord;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.Transfer;
import com.revolut.challenge.model.enumeration.TransferStatusType;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.repository.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class TransferService {
    private final FinancialAccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final AccountTransactionService accountTransactionService;
    private final DSLContext dslContext;

    public TransferService(FinancialAccountRepository accountRepository,
                           TransferRepository transferRepository,
                           AccountTransactionService accountTransactionService,
                           DSLContext dslContext) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.accountTransactionService = accountTransactionService;
        this.dslContext = dslContext;
    }

    /**
     * Transfer money between two accounts, source and target account must be valid and
     * exist and source must have sufficient balance to transfer
     *
     * @param fromAccountNumber source account number
     * @param toAccountNumber   target account number
     * @param amount            amount that would be transfer
     * @param transactionId     transaction Id of transaction for make idempotent
     */
    public void createTransfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String transactionId) {

        if (fromAccountNumber.equals(toAccountNumber))
            throw new SameAccountException();

        FinancialAccount fromAccount = accountRepository.getByAccountNumber(fromAccountNumber);
        FinancialAccount toAccount = accountRepository.getByAccountNumber(toAccountNumber);

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency()))
            throw new IncompatibleCurrencyException();

        TransferRecord transferRecord = transferRepository.save(Transfer.builder()
                .transferStatusType(TransferStatusType.CREATE)
                .fromAccount(fromAccount.getId())
                .toAccount(toAccount.getId())
                .createDatetime(LocalDateTime.now())
                .build());

        dslContext.transaction(configuration -> {
            var transactionCtx = DSL.using(configuration);

            log.info("Create transfer from account {} to account {} with amount {}.", fromAccountNumber, toAccountNumber, amount);

            try {
                accountTransactionService.createWithdrawTransaction(transactionCtx, fromAccountNumber, amount, transactionId, transferRecord.getId());
            } catch (InsufficientBalanceException e) {
                log.info("Could not transfer from account {} because amount {} is more than account balance ({}).", fromAccountNumber, amount, fromAccount.getBalance());
                transferRepository.updateState(transferRecord.getId(), TransferStatusType.INSUFFICIENT_BALANCE);
                throw e;
            } catch (Exception e) {
                log.warn("Error in transfer from account {} to account {} with amount {}.", fromAccountNumber, toAccountNumber, amount);
                transferRepository.updateState(transferRecord.getId(), TransferStatusType.ERROR);
                throw e;
            }

            try {
                accountTransactionService.createDepositTransaction(transactionCtx, toAccountNumber, amount, transactionId, transferRecord.getId());
                transferRepository.updateState(transferRecord.getId(), TransferStatusType.DONE);
                log.info("Successfully transfer from account {} to account {} with amount {}.", fromAccountNumber, toAccountNumber, amount);
            } catch (Exception e) {
                log.warn("Error in transfer from account {} to account {} with amount {}.", fromAccountNumber, toAccountNumber, amount);
                transferRepository.updateState(transferRecord.getId(), TransferStatusType.ERROR);
                throw e;
            }
        });
    }
}
