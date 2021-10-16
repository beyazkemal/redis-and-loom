package com.kemalbeyaz.worker.redis;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerBase;
import redis.clients.jedis.Jedis;

public class RedisWorker extends WorkerBase<Jedis> {

    public RedisWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);
    }

    @Override
    protected void initializeConnectionPool() {

    }

    @Override
    protected Jedis getConnection() {
        return null;
    }

    @Override
    protected void doSomething(final Jedis connection) {

    }
}
