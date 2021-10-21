package com.kemalbeyaz.worker.mysql;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerBase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLWorker extends WorkerBase<Connection> {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLWorker.class);
    public static final String MYSQL_HOST_ENV = "MYSQL_HOST";
    public static final String MYSQL_USERNAME_ENV = "MYSQL_USERNAME";
    public static final String MYSQL_PASSWORD_ENV = "MYSQL_PASSWORD";
    private static String MYSQL_HOST = "192.168.1.114";
    private static String MYSQL_USERNAME = "admin";
    private static String MYSQL_PASSWORD = "Password12345*";

    private HikariDataSource dataSource;

    public MySQLWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);
        prepareConfigs();
        initializeConnectionPool(taskData);
    }

    @Override
    protected Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("Error occur while getting connection form pool.", e);
            return null;
        }
    }

    @Override
    protected void doSomething(Connection connection) {
        if (connection == null) {
            return;
        }

        final var s = UUID.randomUUID().toString();
        String insert = "INSERT INTO loom (`key`, `value`) VALUES (\'" + s + "\',\'" + s + "\')";
        try (var stmt = connection.createStatement()) {
            stmt.executeUpdate(insert);
        } catch (SQLException e) {
            LOG.error("Error: ", e);
        }
    }

    @Override
    protected void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.warn("Error when closing connection:", e);
        }
    }

    private void prepareConfigs() {
        String mysqlHost = System.getenv(MYSQL_HOST_ENV);
        if (mysqlHost != null && !mysqlHost.isEmpty()) {
            LOG.info("{}: {}", MYSQL_HOST_ENV, mysqlHost);
            MYSQL_HOST = mysqlHost;
        }

        String mysqlUsername = System.getenv(MYSQL_USERNAME_ENV);
        if (mysqlUsername != null && !mysqlUsername.isEmpty()) {
            LOG.info("{}: {}", MYSQL_USERNAME_ENV, mysqlUsername);
            MYSQL_USERNAME = mysqlUsername;
        }

        String mysqlPassword = System.getenv(MYSQL_PASSWORD_ENV);
        if (mysqlPassword != null && !mysqlPassword.isEmpty()) {
            LOG.info("{}: {}", MYSQL_PASSWORD_ENV, mysqlPassword);
            MYSQL_PASSWORD = mysqlPassword;
        }
    }

    private synchronized void initializeConnectionPool(final TaskData taskData) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + MYSQL_HOST + "/super_mysql");
        config.setUsername(MYSQL_USERNAME);
        config.setPassword(MYSQL_PASSWORD);
        config.setJdbcUrl("com.mysql.jdbc.Driver");
        config.setMinimumIdle(taskData.getConnectionCount());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }
}
