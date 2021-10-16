package com.kemalbeyaz.worker;

import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.core.RedisManager;
import com.kemalbeyaz.worker.core.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        var taskLatch = new CountDownLatch(1);
        var redisManager = new RedisManager();

        // redis connect and send message, I'm ready!
        redisManager.sendImReady();

        // redis connect and wait for order :)
        AtomicReference<TaskData> taskDataRef = new AtomicReference<>();
        redisManager.subscribeToChannels(taskDataRef, taskLatch);
        LOG.info("Waiting for task order...");

        taskLatch.await();
        var taskData = taskDataRef.get();
        LOG.info("Task received: {}", taskData);

        var worker = WorkerFactory.getInstance(taskData, redisManager);
        var resultLatch = worker.runTask();

        LOG.info("Waiting for tasks to finish...");
        resultLatch.await();
        LOG.info("All tasks finished.");
    }
}
