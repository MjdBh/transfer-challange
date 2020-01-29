package com.revolut.challenge.web.rest;

import com.revolut.challenge.BaseIT;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.CreateAccountRequestDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;


public class AccountControllerIT extends BaseIT {

    private final FinancialAccountRepository financialAccountRepository = new FinancialAccountRepository(dataContext );

    @Test
    public void createFinancialAccountSuccessful() {

        String accountNumber=
        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(new CreateAccountRequestDTO(Currency.EUR))).
                post("/accounts").then().
                log().all().
                assertThat().
                statusCode(201)
                .body("currency",equalTo("EUR")).extract().path("accountNumber") ;

        var financialAccount=financialAccountRepository.getByAccountNumber(accountNumber);
        assertThat ( financialAccount.getId()).isNotNull();
    }

    @Test
    public void shouldReturnInvalidRequestForInvalidCurrency(){

        given().accept(ContentType.JSON).
                body("{\"currency\":\"IRT\"}").
                post("/accounts").then().
                log().all().
                assertThat().
                statusCode(400).
                body("message",containsString("Invalid body")) ;
    }
}
