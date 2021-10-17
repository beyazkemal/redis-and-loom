package com.kemalbeyaz.worker.core;

import com.kemalbeyaz.shared.dto.ResultData;
import com.kemalbeyaz.shared.dto.TaskData;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.stream.IntStream;

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
    }

    protected abstract T getConnection();

    protected abstract void doSomething(T connection);

    @Override
    public CountDownLatch runTask() {

        IntStream.range(0, taskData.getConnectionCount())
                .parallel()
                .forEach(this::executeFirstLevel);

        return countDownLatch;
    }

    private void executeFirstLevel(int i) {
        executorService.execute(() -> {
            final T connection = getConnection();

            IntStream.range(0, taskData.getLoopCount())
                    .parallel()
                    .forEach(j -> executeSecondLevel(connection));
        });
    }

    private void executeSecondLevel(T connection) {
        Thread.startVirtualThread(() -> {
            final var start = LocalDateTime.now();
            doSomething(connection);
            final var now = LocalDateTime.now();
            pushResultData(start, now);
            countDownLatch.countDown();
        });
    }

    private void pushResultData(final LocalDateTime start, final LocalDateTime end) {
        redisManager.sendResultData(new ResultData(start, end));
    }
}
