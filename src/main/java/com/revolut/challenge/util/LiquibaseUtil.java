package com.revolut.challenge.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.sql.SQLException;

@Log4j2
public final class LiquibaseUtil {

    private static String changeLogPath = "liquibase/master.xml";

    private LiquibaseUtil() {
    }

    /**
     * Migrate Database based on change logs on changeLogPath.
     *
     * @param dataSource for run migration on datasource;
     */

    public static void init(final DataSource dataSource) {
        Database database = null;
        try {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        } catch (DatabaseException e) {
            log.error("Database exception in init liquibase.", e);
        } catch (SQLException e) {
            log.error("SQL exception in init liquibase.", e);
        }

        try {
            var liquibase = new Liquibase(changeLogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update("main");
        } catch (LiquibaseException e) {
            log.error("Liquibase error.", e);
        }

        log.info("Liquibase initialized successfully.");
    }

}
