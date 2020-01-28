package com.revolut.challenge.repository;

import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.jooq.Tables;
import com.revolut.challenge.jooq.tables.records.FinancialAccountRecord;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.Currency;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.Optional;

public class FinancialAccountRepository {
    private final DSLContext dataContext;

    public FinancialAccountRepository(DSLContext dataContext) {
        this.dataContext = dataContext;
    }

    public Optional<FinancialAccount> getById(Long id) {
        return dataContext.selectFrom(Tables.FINANCIAL_ACCOUNT)
                .where(Tables.FINANCIAL_ACCOUNT.ID.eq(id))
                .fetchOptionalInto(FinancialAccount.class);
    }

    public FinancialAccount getByAccountNumber(String accountNumber) {
        return dataContext.selectFrom(Tables.FINANCIAL_ACCOUNT)
                .where(Tables.FINANCIAL_ACCOUNT.ACCOUNT_NUMBER.eq(accountNumber))
                .fetchOptionalInto(FinancialAccount.class).orElseThrow(() -> new InvalidAccountException(accountNumber));
    }

    public int save(FinancialAccount financialAccount) {
        return dataContext.insertInto(Tables.FINANCIAL_ACCOUNT)
                .set(Tables.FINANCIAL_ACCOUNT.ID, financialAccount.getId())
                .set(Tables.FINANCIAL_ACCOUNT.ACCOUNT_NUMBER, financialAccount.getAccountNumber())
                .set(Tables.FINANCIAL_ACCOUNT.BALANCE, financialAccount.getBalance())
                .set(Tables.FINANCIAL_ACCOUNT.CREATE_DATETIME, financialAccount.getCreateDatetime())
                .set(Tables.FINANCIAL_ACCOUNT.CURRENCY, financialAccount.getCurrency())
                .set(Tables.FINANCIAL_ACCOUNT.ACCOUNT_STATUS_TYPE, financialAccount.getAccountStatusType())
                .execute();
    }

    public int updateBalance(DSLContext dslContext, String accountNumber, BigDecimal balance) {
        return dslContext.update(Tables.FINANCIAL_ACCOUNT)
                .set(Tables.FINANCIAL_ACCOUNT.BALANCE, balance)
                .where(Tables.FINANCIAL_ACCOUNT.ACCOUNT_NUMBER.eq(accountNumber))
                .execute();
    }

    public int updateCurrency(DSLContext dslContext, String accountNumber, Currency currency) {
        return dslContext.update(Tables.FINANCIAL_ACCOUNT)
                .set(Tables.FINANCIAL_ACCOUNT.CURRENCY, currency)
                .where(Tables.FINANCIAL_ACCOUNT.ACCOUNT_NUMBER.eq(accountNumber))
                .execute();
    }


    public FinancialAccountRecord lockFinancialAccount(DSLContext dataContext, String accountNumber) {
        return dataContext.selectFrom(Tables.FINANCIAL_ACCOUNT)
                .where(Tables.FINANCIAL_ACCOUNT.ACCOUNT_NUMBER.eq(accountNumber))
                .forUpdate().fetchOptional().orElseThrow(() -> new InvalidAccountException(accountNumber));
    }
}
