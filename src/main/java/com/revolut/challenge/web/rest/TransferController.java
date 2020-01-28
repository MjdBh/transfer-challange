package com.revolut.challenge.web.rest;


import com.revolut.challenge.exception.ConstraintViolationException;
import com.revolut.challenge.service.TransferService;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.CreateWithdrawResponseDTO;
import com.revolut.challenge.web.dto.TransferMoneyRequestDTO;
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
public class TransferController {

    private TransferService transferService;
    private Validator validator;

    public TransferController(TransferService transferService, Validator validator) {
        this.transferService = transferService;
        this.validator = validator;
    }

    private void validateDTO(Object requestDTO) {
        var constraints = validator.validate(requestDTO);
        if (!constraints.isEmpty()) {
            log.error(constraints);
            throw new ConstraintViolationException(constraints);
        }
    }

    public Route transfer = (Request request, Response response) -> {

        var requestDTO = JsonUtils.convertToObject(request.body(), TransferMoneyRequestDTO.class);
        validateDTO(requestDTO);

        String transactionId = getTransactionId(requestDTO.getTransactionId());
        transferService.createTransfer(requestDTO.getFromAccount(),
                requestDTO.getToAccount(),
                new BigDecimal(requestDTO.getAmount()),
                transactionId);

        response.status(HttpStatus.CREATED_201);
        log.info("Transfer from account-number={} to account-number={} with amount={} and transaction-id={}.", requestDTO.getFromAccount(), requestDTO.getToAccount(), requestDTO.getAmount(), transactionId);
        return new CreateWithdrawResponseDTO(transactionId);
    };


    private String getTransactionId(String transactionId) {
        return StringUtils.isEmpty(transactionId) ? UUID.randomUUID().toString() : transactionId;
    }
}
