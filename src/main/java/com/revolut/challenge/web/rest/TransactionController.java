package com.revolut.challenge.web.rest;


import com.revolut.challenge.exception.ConstraintViolationException;
import com.revolut.challenge.service.AccountTransactionService;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.AccountTransactionDetails;
import com.revolut.challenge.web.dto.CreateDepositRequestDTO;
import com.revolut.challenge.web.dto.CreateWithdrawRequestDTO;
import com.revolut.challenge.web.dto.CreateWithdrawResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.UUID;

@Log4j2
public class TransactionController {

    private AccountTransactionService accountTransactionService;
    private Validator validator;

    public TransactionController(AccountTransactionService accountTransactionService, Validator validator) {
        this.accountTransactionService = accountTransactionService;
        this.validator = validator;
    }

    private void validateDTO(Object requestDTO) {
        var constraints = validator.validate(requestDTO);
        if (!constraints.isEmpty()) {
            log.error(constraints);
            throw new ConstraintViolationException(constraints);
        }
    }

    public Route createWithdraw = (Request request, Response response) -> {

        var requestDTO = JsonUtils.convertToObject(request.body(), CreateWithdrawRequestDTO.class);
        validateDTO(requestDTO);
        String transactionId = getTransactionId(requestDTO.getTransactionId());

        accountTransactionService.createWithdrawTransaction(requestDTO.getAccountNumber(),
                new BigDecimal(requestDTO.getAmount()), transactionId, null);

        response.status(HttpStatus.CREATED_201);
        log.info("Withdraw from account-number={} with amount={} and transaction-id={}.", requestDTO.getAccountNumber(), requestDTO.getAmount(), transactionId);
        return new CreateWithdrawResponseDTO(transactionId);
    };


    public Route createDeposit = (Request request, Response response) -> {

        var requestDTO = JsonUtils.convertToObject(request.body(), CreateDepositRequestDTO.class);
        validateDTO(requestDTO);
        String transactionId = getTransactionId(requestDTO.getTransactionId());

        accountTransactionService.createDepositTransaction(requestDTO.getAccountNumber(),
                new BigDecimal(requestDTO.getAmount()),
                transactionId, null);

        response.status(HttpStatus.CREATED_201);
        log.info("Deposit from account-number={} with amount={} and transaction-id={}.", requestDTO.getAccountNumber(), requestDTO.getAmount(), transactionId);
        return new CreateWithdrawResponseDTO(transactionId);
    };

    public Route getAccountTransactionList = (Request request, Response response) -> {

        String accountNumber = request.params().get(":account_number");
        AccountTransactionDetails accountTransActionList = accountTransactionService.getAccountTransActionList(accountNumber);

        response.status(HttpStatus.CREATED_201);
        log.info("Get Account transaction for account_number={}", accountNumber);
        return accountTransActionList;
    };

    private String getTransactionId(String transactionId) {
        return StringUtils.isEmpty(transactionId) ? UUID.randomUUID().toString() : transactionId;
    }
}
