package com.revolut.challenge.web.rest;

import com.revolut.challenge.BaseIT;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.model.enumeration.TransferStatusType;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.repository.TransferRepository;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.TransferMoneyRequestDTO;
import io.restassured.http.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class TransferControllerIT extends BaseIT {

    private static final FinancialAccountRepository financialAccountRepository = new FinancialAccountRepository(dataContext);
    private static final TransferRepository transferRepository = new TransferRepository(dataContext);

    @BeforeAll
    public static void prepareData() {
        financialAccountRepository.save(FinancialAccount.builder()
                .accountNumber("6666")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("500"))
                .currency(Currency.USD)
                .build());

        financialAccountRepository.save(FinancialAccount.builder()
                .accountNumber("7777")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("1000"))
                .currency(Currency.USD)
                .build());
    }

    @Test
    public void shouldTransferBetweenGivenDifferentAccount() {

        String fromAccountNumber = "6666";
        String toAccountNumber = "7777";

        financialAccountRepository.updateBalance(dataContext, fromAccountNumber, new BigDecimal("500"));
        financialAccountRepository.updateCurrency(dataContext, fromAccountNumber, Currency.USD);
        financialAccountRepository.updateBalance(dataContext, toAccountNumber, new BigDecimal("1000"));
        financialAccountRepository.updateCurrency(dataContext, toAccountNumber, Currency.USD);

        String transactionId = UUID.randomUUID().toString();

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new TransferMoneyRequestDTO(transactionId, fromAccountNumber, toAccountNumber, 100L)))
                .post( "/accounts/transfer")
                .then().log().all()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId));

        FinancialAccount fromAccount = financialAccountRepository.getByAccountNumber(fromAccountNumber);
        FinancialAccount toAccount = financialAccountRepository.getByAccountNumber(toAccountNumber);

        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("400.00"));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("1100.00"));
        assertThat(transferRepository.getTransferByAccounts(fromAccount.getId(), toAccount.getId(), TransferStatusType.DONE).isPresent());
    }

    @Test
    public void shouldReturn400IfAmountBiggerThanFromAccountBalance() {

        String fromAccountNumber = "6666";
        String toAccountNumber = "7777";

        financialAccountRepository.updateBalance(dataContext, fromAccountNumber, new BigDecimal("1000"));
        financialAccountRepository.updateBalance(dataContext, toAccountNumber, new BigDecimal("1000"));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new TransferMoneyRequestDTO(UUID.randomUUID().toString(), fromAccountNumber, toAccountNumber, 60000L)))
                .post( "/accounts/transfer")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    public void shouldReturn400IfTwoAccountBeSame() {

        String fromAccountNumber = "6666";
        String toAccountNumber = "6666";

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new TransferMoneyRequestDTO(UUID.randomUUID().toString(), fromAccountNumber, toAccountNumber, 600L)))
                .post( "/accounts/transfer")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    public void shouldReturn400IfAccountCurrencyIsIncompatible() {

        String fromAccountNumber = "6666";
        String toAccountNumber = "7777";

        financialAccountRepository.updateCurrency(dataContext, fromAccountNumber, Currency.USD);
        financialAccountRepository.updateCurrency(dataContext, toAccountNumber, Currency.EUR);

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new TransferMoneyRequestDTO(UUID.randomUUID().toString(), fromAccountNumber, toAccountNumber, 60000L)))
                .post( "/accounts/transfer")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }
}
