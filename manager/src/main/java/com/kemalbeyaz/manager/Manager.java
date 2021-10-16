package com.kemalbeyaz.manager;

import com.kemalbeyaz.manager.cluster.AwsClusterManager;
import com.kemalbeyaz.manager.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class Manager {

    private static final Logger LOG = LoggerFactory.getLogger(Manager.class);

    private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    private static final ExecutorService executorService =
            new ThreadPoolExecutor(3, 3, 0, TimeUnit.MILLISECONDS, workQueue);

    public static void main(String[] args) throws InterruptedException {
        Arguments arguments = new Arguments(args);
        LOG.info("Hello from manager: {}", arguments);

        RedisManager redisManager = new RedisManager(arguments);
        executorService.execute(redisManager::subscribeToServiceStartedChannel);

        AwsClusterManager clusterManager = new AwsClusterManager();
        clusterManager.createService(arguments.getServiceName(), arguments.getServiceCount());

        LOG.info("Waiting for all services to start...");
        redisManager.getCountDownLatchForServiceCreation().await();
        executorService.execute(redisManager::unSubscribeToServiceStartedTopic);
        executorService.execute(() -> redisManager.publishToTaskTopic(arguments.toTaskData()));
        TimeUnit.SECONDS.sleep(3);

        // delete services after job is done!
        LOG.info("Waiting for all tasks to finish...");
        redisManager.getCountDownLatchForTaskFinished().await();
        clusterManager.deleteService(arguments.getServiceName());

        // calculate data and write txt
        redisManager.getResultDataSet().forEach(a -> System.out.println(a.toString()));

        TimeUnit.SECONDS.sleep(10);
        System.exit(1);
    }
}
