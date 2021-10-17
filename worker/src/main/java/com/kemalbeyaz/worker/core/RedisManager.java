package com.kemalbeyaz.worker.core;

import com.kemalbeyaz.shared.RedisManagerBase;
import com.kemalbeyaz.shared.dto.ResultData;
import com.kemalbeyaz.shared.dto.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class RedisManager extends RedisManagerBase {

    private static final Logger LOG = LoggerFactory.getLogger(RedisManager.class);

    private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
    private static final ExecutorService executorService =
            new ThreadPoolExecutor(20, 40, 0, TimeUnit.MILLISECONDS, workQueue);

    public void subscribeToChannels(final AtomicReference<TaskData> taskDataRef, final CountDownLatch taskLatch) {
        var jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equals(TASK_CH)) {
                    var taskData = TaskData.fromJSON(message);
                    taskDataRef.set(taskData);
                    LOG.info("Task: {}", taskData);

                    unsubscribe(TASK_CH);
                    taskLatch.countDown();
                }
            }
        };

        executorService.execute(() -> {
            try (var jedis = initialize()) {
                jedis.subscribe(jedisPubSub, TASK_CH);
            }
        });
    }

    public void sendImReady() {
        executorService.execute(() -> {
            try (var jedis = initialize()) {
                jedis.publish(SERVICE_STARTED_CH, "I'm ready!");
            }
        });
    }

    public void sendResultData(final ResultData resultData) {
        executorService.execute(() -> {
            try (var jedis = initialize()) {
                LOG.info("Result: {}", resultData.toJSON());
                jedis.publish(TASK_FINISHED_CH, resultData.toJSON());
            }
        });
    }
}
