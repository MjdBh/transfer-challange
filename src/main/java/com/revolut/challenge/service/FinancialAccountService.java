package com.revolut.challenge.service;

import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.web.dto.FinancialAccountDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;

    public FinancialAccountService(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

    /**
     * create financial account
     * @param financialAccount financial account data
     */
    public void createAccount(FinancialAccount financialAccount) {

        financialAccountRepository.save(financialAccount);
        log.info("Financial account by id={} created", financialAccount.getAccountNumber());
    }

    /**
     * Get account information for given account number
     * @param accountNumber account number
     * @return account information
     */
    public FinancialAccountDTO getAccount(String accountNumber){
        FinancialAccount account = financialAccountRepository.getByAccountNumber(accountNumber);
        return FinancialAccountDTO.builder()
                .accountNumber(account.getAccountNumber())
                .accountStatusType(account.getAccountStatusType())
                .createDatetime(account.getCreateDatetime())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();

    }
}
