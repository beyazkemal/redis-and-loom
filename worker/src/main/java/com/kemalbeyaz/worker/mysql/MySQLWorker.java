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

public class MySQLWorker extends WorkerBase<Connection> {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLWorker.class);
    private HikariDataSource dataSource;

    public MySQLWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);
    }

    @Override
    protected Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("Error occur while getting connection form pool.", e);
            throw new RuntimeException();
        }
    }

    @Override
    protected void doSomething(Connection connection) {

    }

    private synchronized void initializeConnectionPool(final TaskData taskData) {
        HikariConfig config = new HikariConfig();
        config.setUsername("");
        config.setPassword("");
        config.setJdbcUrl("");
        config.setMinimumIdle(taskData.getConnectionCount());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }
}
