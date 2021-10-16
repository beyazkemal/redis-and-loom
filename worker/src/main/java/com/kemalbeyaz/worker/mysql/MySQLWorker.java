package com.kemalbeyaz.worker.mysql;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLWorker extends WorkerBase<String> {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLWorker.class);

    public MySQLWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);
    }

    @Override
    protected void initializeConnectionPool() {

    }

    @Override
    protected String getConnection() {
        return null;
    }

    @Override
    protected void doSomething(String connection) {

    }
}
