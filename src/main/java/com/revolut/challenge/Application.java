package com.revolut.challenge;

import com.revolut.challenge.exception.ConstraintViolationException;
import com.revolut.challenge.exception.TransferBaseException;
import com.revolut.challenge.repository.AccountTransactionRepository;
import com.revolut.challenge.repository.FinancialAccountRepository;
import com.revolut.challenge.repository.TransferRepository;
import com.revolut.challenge.service.AccountTransactionService;
import com.revolut.challenge.service.FinancialAccountService;
import com.revolut.challenge.service.TransferService;
import com.revolut.challenge.util.JsonUtils;
import com.revolut.challenge.web.rest.AccountController;
import com.revolut.challenge.web.rest.TransactionController;
import com.revolut.challenge.web.rest.TransferController;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import spark.Spark;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

import static spark.Spark.*;


@Log4j2
public final class Application {

    public static final String CONTEXT_PATH = "/api/v1";
    private static final Integer MIN_THREAD_SIZE = Integer.valueOf(System.getProperty("http-min-thread", "3"));
    private static final Integer MAX_THREAD_SIZE = Integer.valueOf(System.getProperty("http-max-thread", "10"));
    private static final Integer IDEL_TIME_MILLIS = Integer.valueOf(System.getProperty("http-timeout", "1000"));
    public static  final String JSON_CONTENT_TYPE="application/json";

    public Application(final DataSource dataSource, String port) {

        DSLContext dataContext = DSL.using(dataSource, SQLDialect.H2);
        var factory = Validation.buildDefaultValidatorFactory();

        var validator = factory.getValidator();
        var financialAccountRepository = new FinancialAccountRepository(dataContext);
        var transferRepository = new TransferRepository(dataContext);
        var accountTransactionRepository = new AccountTransactionRepository(dataContext);


        //  Initializing service beans
        var financialAccountService = new FinancialAccountService(financialAccountRepository);
        var accountTransactionService = new AccountTransactionService(accountTransactionRepository, financialAccountRepository, dataContext);
        var transferService = new TransferService(financialAccountRepository, transferRepository, accountTransactionService);

        var accountController = new AccountController(financialAccountService, validator);
        var transactionController = new TransactionController(accountTransactionService, validator);
        var transferController = new TransferController(transferService, validator);

        Spark.threadPool(MAX_THREAD_SIZE, MIN_THREAD_SIZE, IDEL_TIME_MILLIS);
        Spark.port(Integer.parseInt(port));
        Spark.after((req, res) -> res.type(JSON_CONTENT_TYPE));
        Spark.exception(TransferBaseException.class, (e, request, response) -> {
            response.status(e.getStatus());
            response.type(JSON_CONTENT_TYPE);
            response.body(JsonUtils.writeValueAsString(e.getDetail()));
            log.error("Error on processing {}", request.pathInfo());
        });

        Spark.exception(ConstraintViolationException.class, (e, request, response) -> {
            response.type(JSON_CONTENT_TYPE);
            response.status(400);
            var message = e.getViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(System.lineSeparator()));
            response.body(String.format("{\"message\":\"%s\" }", message));
        });

        path(CONTEXT_PATH, () -> {
                    before("/*", (q, a) -> log.info("Received api call on  path {}", q.pathInfo()));
                    path("/", () -> {
                        path("/accounts", () -> {
                            post("", accountController.createFinancialAccount, JsonUtils::writeValueAsString);
                            get("/:account_number", accountController.getAccount, JsonUtils::writeValueAsString);
                            post("/withdraw", transactionController.createWithdraw, JsonUtils::writeValueAsString);
                            post("/deposit", transactionController.createDeposit, JsonUtils::writeValueAsString);
                            post("/transfer", transferController.transfer, JsonUtils::writeValueAsString);
                            get("/:account_number/transaction", transactionController.getAccountTransactionList, JsonUtils::writeValueAsString);
                        });
                    });
                }
        );


    }

    public void logApplicationStartup(String port) {
        String protocol = "http";
        String contextPath = System.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n" +
                        "----------------------------------------------------------\n",
                "Revolut Transfer",
                protocol,
                port,
                contextPath,
                protocol,
                hostAddress,
                port,
                contextPath);
    }


    public void stop() {
        log.info("Stop spark .");
        Spark.stop();

    }

}
