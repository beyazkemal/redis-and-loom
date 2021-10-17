package com.kemalbeyaz.manager.redis;

import com.kemalbeyaz.manager.Arguments;
import com.kemalbeyaz.shared.RedisManagerBase;
import com.kemalbeyaz.shared.dto.ResultData;
import com.kemalbeyaz.shared.dto.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class RedisManager extends RedisManagerBase {

    private static final Logger LOG = LoggerFactory.getLogger(RedisManager.class);
    private final Set<ResultData> resultDataSet = new HashSet<>();
    private final CountDownLatch countDownLatchForServiceCreation;
    private final CountDownLatch countDownLatchForTaskFinished;

    public RedisManager(final Arguments arguments) {
        super();
        this.countDownLatchForServiceCreation = new CountDownLatch(arguments.getServiceCount());
        this.countDownLatchForTaskFinished = new CountDownLatch(calculateLatchSize(arguments));
        LOG.info("Redis Manager initialized.");
    }

    public void subscribeToServiceStartedChannel() {
        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {

                if (channel.equals(SERVICE_STARTED_CH)) {
                    if (message.equals(UNSUBSCRIBE)) {
                        LOG.info("Unsubscribed from {}", SERVICE_STARTED_CH);
                        unsubscribe(SERVICE_STARTED_CH);
                        return;
                    }

                    LOG.info(message);
                    countDownLatchForServiceCreation.countDown();
                }

                if (channel.equals(TASK_FINISHED_CH)) {
                    ResultData resultData = ResultData.fromJSON(message);
                    resultDataSet.add(resultData);
                    countDownLatchForTaskFinished.countDown();
                    LOG.info("{} Finished: {}", countDownLatchForTaskFinished.getCount(), resultData.toString());
                }
            }
        };

        try (var jedis = initialize()) {
            jedis.subscribe(jedisPubSub, SERVICE_STARTED_CH, TASK_FINISHED_CH);
        }

        LOG.info("Subscribed to the service started and task finished channels.");
    }

    public void unSubscribeToServiceStartedTopic() {
        try (var jedis = initialize()) {
            jedis.publish(SERVICE_STARTED_CH, UNSUBSCRIBE);
        }
    }

    public void publishToTaskTopic(final TaskData taskData) {
        try (var jedis = initialize()) {
            jedis.publish(TASK_CH, taskData.toJSON());
            LOG.info("Task send.");
        }
    }

    public Set<ResultData> getResultDataSet() {
        return resultDataSet;
    }

    public CountDownLatch getCountDownLatchForServiceCreation() {
        return countDownLatchForServiceCreation;
    }

    public CountDownLatch getCountDownLatchForTaskFinished() {
        return countDownLatchForTaskFinished;
    }

    private int calculateLatchSize(final Arguments arguments) {
        return arguments.getServiceCount() * arguments.getConnectionCount() * arguments.getLoopCount();
    }
}
