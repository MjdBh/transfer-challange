package com.revolut.challenge.web.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateDepositRequestDTOTest extends BaseDTOTest {

    @Test
    public void createDepositRequestAcceptOnlyValidValue() {
        CreateDepositRequestDTO invalidRequest = new CreateDepositRequestDTO();

        Set<ConstraintViolation<CreateDepositRequestDTO>> constraintViolations = validator.validate(invalidRequest);
        assertEquals(2,constraintViolations.size());

        Set<String> messages = Set.of("Account number cant be null", "Amount cant be null", "Amount must be greater than 0");

        for (ConstraintViolation<CreateDepositRequestDTO> constraintViolation : constraintViolations) {
            assertThat(messages.contains(constraintViolation.getMessage()));
        }
    }

    @Test
    public void createDepositRequestAcceptOnlyValidAccountNumber() {
        CreateDepositRequestDTO validRequest = new CreateDepositRequestDTO();
        validRequest.setAccountNumber(UUID.randomUUID().toString());
        validRequest.setAmount(1000L);
        validRequest.setTransactionId(UUID.randomUUID().toString());

        assertEquals(0,validator.validate(validRequest).size());
    }

    @Test
    public void createDepositRequestAcceptOnlyNotNullAmount() {
        CreateDepositRequestDTO validRequest = new CreateDepositRequestDTO();
        validRequest.setAccountNumber(UUID.randomUUID().toString());
        validRequest.setAmount(1000L);
        validRequest.setTransactionId(UUID.randomUUID().toString());

        assertEquals(0,validator.validate(validRequest).size());
    }

    @Test
    public void createDepositRequestAcceptOnlyPositiveAmount() {
        CreateDepositRequestDTO invalidRequest = new CreateDepositRequestDTO();
        invalidRequest.setAccountNumber(UUID.randomUUID().toString());
        invalidRequest.setAmount(-1000L);
        invalidRequest.setTransactionId(UUID.randomUUID().toString());

        assertEquals(1,validator.validate(invalidRequest).size());
    }
}