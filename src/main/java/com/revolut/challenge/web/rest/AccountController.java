package com.revolut.challenge.web.rest;


import com.revolut.challenge.exception.ConstraintViolationException;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.service.FinancialAccountService;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.CreateAccountRequestDTO;
import com.revolut.challenge.web.dto.CreateAccountResponseDTO;
import com.revolut.challenge.web.dto.FinancialAccountDTO;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
public class AccountController {

    private FinancialAccountService financialAccountService;
    private Validator validator;

    public AccountController(FinancialAccountService financialAccountService, Validator validator) {
        this.financialAccountService = financialAccountService;
        this.validator = validator;
    }

    private void validateDTO(Object requestDTO) {
        var constraints = validator.validate(requestDTO);
        if (!constraints.isEmpty()) {
            log.error(constraints);
            throw new ConstraintViolationException(constraints);
        }
    }

    public Route createFinancialAccount = (Request request, Response response) -> {

        var requestDTO = JsonUtils.convertToObject(request.body(), CreateAccountRequestDTO.class);
        validateDTO(requestDTO);

        var financialAccount = FinancialAccount.builder().
                accountNumber(UUID.randomUUID().toString()).
                accountStatusType(AccountStatusType.ACTIVE).
                balance(BigDecimal.ZERO).
                createDatetime(LocalDateTime.now()).
                currency(requestDTO.getCurrency()).
                build();
        financialAccountService.createAccount(financialAccount);
        log.info("Account number = {}  created.", financialAccount.getAccountNumber());
        response.status(HttpStatus.CREATED_201);
        return new CreateAccountResponseDTO(financialAccount.getAccountNumber(), financialAccount.getCurrency());
    };

    public Route getAccount = (Request request, Response response) -> {

        String accountNumber = request.params().get(":account_number");
        FinancialAccountDTO account = financialAccountService.getAccount(accountNumber);
        response.status(HttpStatus.CREATED_201);

        log.info("Get account with number {}.",accountNumber);
        return account;
    };
}
