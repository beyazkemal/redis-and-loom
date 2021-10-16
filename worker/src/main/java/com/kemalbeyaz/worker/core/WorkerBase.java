package com.kemalbeyaz.worker.core;

import com.kemalbeyaz.shared.dto.ResultData;
import com.kemalbeyaz.shared.dto.TaskData;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public abstract class WorkerBase<T> implements Worker {

    private final TaskData taskData;
    private final RedisManager redisManager;
    private final CountDownLatch countDownLatch;
    private final ExecutorService executorService;

    public WorkerBase(final TaskData taskData, final RedisManager redisManager) {
        this.taskData = taskData;
        this.redisManager = redisManager;

        this.countDownLatch = new CountDownLatch(taskData.getConnectionCount() * taskData.getLoopCount());
        final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
        this.executorService =
                new ThreadPoolExecutor(taskData.getConnectionCount(), taskData.getConnectionCount(), 0, TimeUnit.MILLISECONDS, workQueue);
        initializeConnectionPool();
    }

    protected abstract void initializeConnectionPool();

    protected abstract T getConnection();

    protected abstract void doSomething(T connection);

    @Override
    public CountDownLatch runTask() {

        for (int i = 0; i < taskData.getConnectionCount(); i++) {
            executorService.execute(() -> {
                final T connection = getConnection();

                for (int j = 0; j < taskData.getLoopCount(); j++) {

                    Thread.startVirtualThread(() -> {
                        final var start = LocalDateTime.now();
                        doSomething(connection);
                        final var now = LocalDateTime.now();
                        pushResultData(start, now);
                        countDownLatch.countDown();
                    });
                }
            });
        }

        return countDownLatch;
    }

    private void pushResultData(final LocalDateTime start, final LocalDateTime end) {
        redisManager.sendResultData(new ResultData(start, end));
    }
}
