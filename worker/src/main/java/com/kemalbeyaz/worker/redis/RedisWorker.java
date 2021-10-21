package com.kemalbeyaz.worker.redis;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.UUID;

import static com.kemalbeyaz.shared.RedisManagerBase.REDIS_HOST_ENV;

public class RedisWorker extends WorkerBase<Pipeline> {

    private static final Logger LOG = LoggerFactory.getLogger(RedisWorker.class);

    static final String KEY_PREFIX = "key-";
    private static String REDIS_HOST = "192.168.1.114";

    public RedisWorker(final TaskData taskData, final RedisManager redisManager) {
        super(taskData, redisManager);

        String redisHost = System.getenv(REDIS_HOST_ENV);
        if (redisHost != null && !redisHost.isEmpty()) {
            LOG.info("{}: {}", REDIS_HOST_ENV, redisHost);
            REDIS_HOST = redisHost;
        }
    }

    @Override
    protected Pipeline getConnection() {
        return initializeRedisConnection().pipelined();
    }

    @Override
    protected void doSomething(final Pipeline connection) {
        final var s = UUID.randomUUID().toString();
        connection.set(KEY_PREFIX + s, s);
    }

    @Override
    protected void closeConnection(Pipeline connection) {
        connection.close();
    }

    protected synchronized static Jedis initializeRedisConnection() {
        var hp = new HostAndPort(REDIS_HOST, 6379);
        return new Jedis(hp);
    }
}
