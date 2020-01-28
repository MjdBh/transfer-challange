package com.revolut.challenge.web.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateWithdrawRequestDTOTest extends BaseDTOTest {

    @Test
    public void createWithdrawRequestAcceptOnlyValidValue() {
        CreateWithdrawRequestDTO invalidRequest = new CreateWithdrawRequestDTO();
        Set<ConstraintViolation<CreateWithdrawRequestDTO>> constraintViolations = validator.validate(invalidRequest);
        assertEquals(2,constraintViolations.size());

        Set<String> messages = Set.of("Account number cant be null", "Amount cant be null", "Amount must be greater than 0");

        for (ConstraintViolation<CreateWithdrawRequestDTO> constraintViolation : constraintViolations) {
            assertThat(messages.contains(constraintViolation.getMessage()));
        }
    }

    @Test
    public void createWithdrawRequestAcceptOnlyValidAccountNumber() {
        CreateWithdrawRequestDTO withdrawRequestDTO = new CreateWithdrawRequestDTO();
        withdrawRequestDTO.setAccountNumber(UUID.randomUUID().toString());
        withdrawRequestDTO.setAmount(1000L);
        withdrawRequestDTO.setTransactionId(UUID.randomUUID().toString());

        assertEquals(0,validator.validate(withdrawRequestDTO).size());
    }

    @Test
    public void createWithdrawRequestAcceptNotNullValidAmount() {
        CreateWithdrawRequestDTO withdrawRequestDTO = new CreateWithdrawRequestDTO();
        withdrawRequestDTO.setAccountNumber(UUID.randomUUID().toString());
        withdrawRequestDTO.setAmount(1000L);
        withdrawRequestDTO.setTransactionId(UUID.randomUUID().toString());

        assertEquals(0,validator.validate(withdrawRequestDTO).size());

    }

    @Test
    public void createWithdrawRequestAcceptOnlyPositiveAmount() {
        CreateWithdrawRequestDTO withdrawRequestDTO = new CreateWithdrawRequestDTO();
        withdrawRequestDTO.setAccountNumber(UUID.randomUUID().toString());
        withdrawRequestDTO.setAmount(-1000L);
        withdrawRequestDTO.setTransactionId(UUID.randomUUID().toString());

        assertEquals(1,validator.validate(withdrawRequestDTO).size());
    }
}