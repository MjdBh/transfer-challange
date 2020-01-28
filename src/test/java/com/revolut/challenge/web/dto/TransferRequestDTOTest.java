package com.revolut.challenge.web.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferRequestDTOTest extends BaseDTOTest {

    @Test
    public void transferRequestAcceptOnlyValidValue() {
        TransferMoneyRequestDTO transferMoneyRequestDTO = new TransferMoneyRequestDTO();
        Set<ConstraintViolation<TransferMoneyRequestDTO>> constraintViolations = validator.validate(transferMoneyRequestDTO);

        assertEquals(constraintViolations.size(), 3);

        Set<String> messages = Set.of("From account number can not be null",
                "To account number can not be null",
                "Amount can not be null",
                "Amount must be greater than 0");
        for (ConstraintViolation<TransferMoneyRequestDTO> constraintViolation : constraintViolations) {
            assertThat(messages.contains(constraintViolation.getMessage()));
        }
    }

    @Test
    public void transferRequestAcceptOnlyValidFromAccountNumber() {
        TransferMoneyRequestDTO transferMoneyRequestDTO = new TransferMoneyRequestDTO();
        transferMoneyRequestDTO.setFromAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setToAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setAmount(1000L);
        transferMoneyRequestDTO.setTransactionId(UUID.randomUUID().toString());

        assertEquals(validator.validate(transferMoneyRequestDTO).size(), 0);
    }

    @Test
    public void transferRequestAcceptOnlyValidToAccountNumber() {
        TransferMoneyRequestDTO transferMoneyRequestDTO = new TransferMoneyRequestDTO();
        transferMoneyRequestDTO.setFromAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setToAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setAmount(1000L);
        transferMoneyRequestDTO.setTransactionId(UUID.randomUUID().toString());

        assertEquals(validator.validate(transferMoneyRequestDTO).size(), 0);
    }


    @Test
    public void createWithdrawRequestAcceptOnlyNotNullAmount() {
        TransferMoneyRequestDTO transferMoneyRequestDTO = new TransferMoneyRequestDTO();
        transferMoneyRequestDTO.setFromAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setToAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setTransactionId(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setAmount(1000L);

        assertEquals(validator.validate(transferMoneyRequestDTO).size(), 0);
    }

    @Test
    public void createWithdrawRequestAcceptOnlyPositiveAmount() {
        TransferMoneyRequestDTO transferMoneyRequestDTO = new TransferMoneyRequestDTO();
        transferMoneyRequestDTO.setFromAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setToAccount(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setTransactionId(UUID.randomUUID().toString());
        transferMoneyRequestDTO.setAmount(-1000L);

        assertEquals(validator.validate(transferMoneyRequestDTO).size(), 1);
    }
}