package com.revolut.challenge.repository;

import com.revolut.challenge.jooq.Tables;
import com.revolut.challenge.model.AccountTransaction;
import com.revolut.challenge.model.enumeration.TransactionType;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;


public class AccountTransactionRepository {

    private final DSLContext dataContext;

    public AccountTransactionRepository(DSLContext dataContext) {
        this.dataContext = dataContext;
    }

    public Optional<AccountTransaction> getById(Long id) {
        return dataContext.selectFrom(Tables.ACCOUNT_TRANSACTION)
                .where(Tables.ACCOUNT_TRANSACTION.ID.eq(id))
                .fetchOptionalInto(AccountTransaction.class);
    }

    public int save(AccountTransaction accountTransaction) {
        return dataContext.insertInto(Tables.ACCOUNT_TRANSACTION)
                .set(Tables.ACCOUNT_TRANSACTION.ID, accountTransaction.getId())
                .set(Tables.ACCOUNT_TRANSACTION.FINANCIAL_ACCOUNT, accountTransaction.getFinancialAccount())
                .set(Tables.ACCOUNT_TRANSACTION.AMOUNT, accountTransaction.getAmount())
                .set(Tables.ACCOUNT_TRANSACTION.BALANCE, accountTransaction.getBalance())
                .set(Tables.ACCOUNT_TRANSACTION.CREATE_DATETIME, accountTransaction.getCreateDatetime())
                .set(Tables.ACCOUNT_TRANSACTION.TRANSFER, accountTransaction.getTransfer())
                .set(Tables.ACCOUNT_TRANSACTION.TRANSACTION_TYPE, accountTransaction.getTransactionType())
                .set(Tables.ACCOUNT_TRANSACTION.TRANSACTION_ID, accountTransaction.getTransactionId())
                .execute();
    }

    public boolean isTransactionPresent(String transactionId, TransactionType transactionType) {
        return dataContext.selectFrom(Tables.ACCOUNT_TRANSACTION)
                .where(Tables.ACCOUNT_TRANSACTION.TRANSACTION_ID.eq(transactionId))
                .and(Tables.ACCOUNT_TRANSACTION.TRANSACTION_TYPE.eq(transactionType))
                .fetchOptional().isPresent();
    }

    public List<AccountTransaction> getAccountTransactionList(long accountId) {
        return dataContext.selectFrom(Tables.ACCOUNT_TRANSACTION)
                .where(Tables.ACCOUNT_TRANSACTION.FINANCIAL_ACCOUNT.eq(accountId))
                .fetch(a -> AccountTransaction.builder()
                        .transactionId(a.getTransactionId())
                        .createDatetime(a.getCreateDatetime())
                        .transactionType(a.getTransactionType())
                        .financialAccount(a.getFinancialAccount())
                        .transfer(a.getTransfer())
                        .amount(a.getAmount())
                        .balance(a.getBalance())
                        .id(a.getId())
                        .build());
    }
}
