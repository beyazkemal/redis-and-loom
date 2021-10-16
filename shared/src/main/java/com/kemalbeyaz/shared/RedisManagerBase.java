package com.kemalbeyaz.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public abstract class RedisManagerBase {

    private static final Logger LOG = LoggerFactory.getLogger(RedisManagerBase.class);
    protected static String REDIS_HOST = "192.168.1.114";
    protected static final String SERVICE_STARTED_CH = "serviceStarted";
    protected static final String TASK_CH = "task";
    protected static final String TASK_FINISHED_CH = "taskFinished";
    protected static final String UNSUBSCRIBE = "unsubscribe";

    public RedisManagerBase() {
        String redisHost = System.getenv("REDIS_HOST");
        if(redisHost != null && !redisHost.isEmpty()) {
            LOG.info("REDIS HOST: " + redisHost);
            REDIS_HOST = redisHost;
        }
    }

    protected static Jedis initialize() {
        var hp = new HostAndPort(REDIS_HOST, 6379);
        return new Jedis(hp);
    }
}
