package com.revolut.challenge.web.rest;

import com.revolut.challenge.BaseIT;
import com.revolut.challenge.model.FinancialAccount;
import com.revolut.challenge.model.enumeration.AccountStatusType;
import com.revolut.challenge.model.enumeration.Currency;
import com.revolut.challenge.model.enumeration.TransactionType;
import com.revolut.challenge.repository.AccountTransactionRepository;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.dto.CreateDepositRequestDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class TransactionControllerIT extends BaseIT {

    private static final FinancialAccountRepository financialAccountRepository = new FinancialAccountRepository(dataContext);
    private static final AccountTransactionRepository accountTransactionRepository = new AccountTransactionRepository(dataContext);

    @BeforeAll
    public static void prepareData() {
        financialAccountRepository.save(FinancialAccount.builder()
                .accountNumber("4444")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("500"))
                .currency(Currency.USD)
                .build());

        financialAccountRepository.save(FinancialAccount.builder()
                .accountNumber("5555")
                .accountStatusType(AccountStatusType.ACTIVE)
                .createDatetime(LocalDateTime.now())
                .balance(new BigDecimal("1000"))
                .currency(Currency.USD)
                .build());
    }

    @Test
    public void shouldCreateDepositForGivenAccount() {

        String accountNumber = "4444";
        String transactionId = UUID.randomUUID().toString();

        financialAccountRepository.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("accounts/deposit")
                .then().log().all()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId));

        assertThat(financialAccountRepository.getByAccountNumber(accountNumber).getBalance()).isEqualTo(new BigDecimal("600.00"));
        assertThat(accountTransactionRepository.isTransactionPresent(transactionId, TransactionType.DEPOSIT)).isTrue();
    }

    @Test
    public void shouldReturn409IfDepositTransactionIdBeDuplicated() {

        String accountNumber = "4444";
        String transactionId = UUID.randomUUID().toString();

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("accounts/deposit")
                .then().log().all()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("accounts/deposit")
                .then().log().all()
                .statusCode(409);
    }

    @Test
    public void shouldCreateWithdrawForGivenAccount() {

        String accountNumber = "4444";
        String transactionId = UUID.randomUUID().toString();

        financialAccountRepository.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("/accounts/withdraw")
                .then().log().all()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId));

        assertThat(financialAccountRepository.getByAccountNumber(accountNumber).getBalance()).isEqualTo(new BigDecimal("400.00"));
        assertThat(accountTransactionRepository.isTransactionPresent(transactionId, TransactionType.WITHDRAW)).isTrue();

    }

    @Test
    public void shouldReturn400IfAmountBeLowerThanAccountBalance() {

        String accountNumber = "4444";
        String transactionId = UUID.randomUUID().toString();

        financialAccountRepository.updateBalance(dataContext, accountNumber, new BigDecimal("500"));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 1000L)))
                .post("/accounts/withdraw")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void shouldReturn409IfWithdrawTransactionIdBeDuplicated() {

        String accountNumber = "4444";
        String transactionId = UUID.randomUUID().toString();

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("/accounts/withdraw")
                .then().log().all()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId));

        given().accept(ContentType.JSON).
                body(JsonUtils.writeValueAsString(
                        new CreateDepositRequestDTO(transactionId, accountNumber, 100L)))
                .post("/accounts/withdraw")
                .then().log().all()
                .statusCode(409);
    }
}
