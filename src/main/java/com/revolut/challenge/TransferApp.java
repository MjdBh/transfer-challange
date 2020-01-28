package com.revolut.challenge;

import com.revolut.challenge.util.LiquibaseUtil;
import org.h2.jdbcx.JdbcConnectionPool;


class TransferApp {
    public static void main(String[] args)  {

        var dataSource = JdbcConnectionPool.create("jdbc:h2:mem:~/transfer", "sa", "");
        var port = System.getProperty("server.port", "4000");
        var application = new Application(dataSource ,port);

        LiquibaseUtil.init(dataSource);
        application.logApplicationStartup(port);

        Runtime.getRuntime().addShutdownHook(new Thread(application::stop, "TransferShutdownHook"));
    }
}

