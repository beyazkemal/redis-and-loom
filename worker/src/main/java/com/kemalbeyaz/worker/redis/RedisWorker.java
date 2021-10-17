package com.kemalbeyaz.worker.redis;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.UUID;

import static com.kemalbeyaz.shared.RedisManagerBase.REDIS_HOST_ENV;

public class RedisWorker extends WorkerBase<Pipeline> {

    private static final Logger LOG = LoggerFactory.getLogger(RedisWorker.class);

    static final String KEY_PREFIX = "key-";
    private static String REDIS_HOST = "192.168.1.114";
    private JedisPool pool;

    public RedisWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);

        initializePool(taskData.getConnectionCount());
        String redisHost = System.getenv(REDIS_HOST_ENV);
        if (redisHost != null && !redisHost.isEmpty()) {
            LOG.info("{}: {}", REDIS_HOST_ENV, redisHost);
            REDIS_HOST = redisHost;
        }
    }

    @Override
    protected Pipeline getConnection() {
        return pool.getResource().pipelined();
    }

    @Override
    protected void doSomething(final Pipeline connection) {
        try (final var pipelined = getConnection()) {
            final var s = UUID.randomUUID().toString();
            pipelined.set(KEY_PREFIX + s, s);
        }
    }

    private synchronized void initializePool(final int maxTotal) {
        var config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxTotal);

        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);

        pool = new JedisPool(config, REDIS_HOST, 6379);
    }
}
