package com.kemalbeyaz.worker.core;

import com.kemalbeyaz.shared.dto.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class LazyWorker extends WorkerBase<String> {

    private static final Logger LOG = LoggerFactory.getLogger(LazyWorker.class);

    public LazyWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);
    }

    @Override
    protected String getConnection() {
        return null;
    }

    @Override
    protected void doSomething(String connection) {
        LOG.info("Doing something...");

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            LOG.warn("Error occur while doing something: ", e);
        }
    }

    @Override
    protected void closeConnection(String connection) {
        LOG.info("Connection closed.");
    }
}
