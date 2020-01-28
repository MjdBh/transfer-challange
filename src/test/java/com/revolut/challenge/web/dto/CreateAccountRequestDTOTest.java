package com.revolut.challenge.web.dto;

import com.revolut.challenge.model.enumeration.Currency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAccountRequestDTOTest extends BaseDTOTest {


    @Test
    public void accountRequestAcceptOnlyValidCurrency() {
        CreateAccountRequestDTO createAccountRequest = new CreateAccountRequestDTO();
        createAccountRequest.setCurrency(Currency.EUR);
        assertEquals(0, validator.validate(createAccountRequest).size());
    }
}