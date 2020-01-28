package com.revolut.challenge;

import com.revolut.challenge.util.LiquibaseUtil;
import io.restassured.RestAssured;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;

import javax.sql.DataSource;

/**
 * Base Class for tests. It initialize database and applications started and stop
 */
public class BaseIT {

    private static final DataSource DATA_SOURCE = JdbcConnectionPool.create("jdbc:h2:mem:transfer", "sa", "");

    private static final int PORT = 4000;

    private static Application application;

    protected static DSLContext dataContext;

    protected static final String BASE_PATH = "http://127.0.0.1";

    static {
        dataContext = DSL.using(DATA_SOURCE, SQLDialect.H2);
        LiquibaseUtil.init(DATA_SOURCE);
        application = new Application(DATA_SOURCE, String.valueOf(PORT));
        RestAssured.baseURI = BASE_PATH;
        RestAssured.port = PORT;
        RestAssured.basePath = Application.CONTEXT_PATH;

    }
}
