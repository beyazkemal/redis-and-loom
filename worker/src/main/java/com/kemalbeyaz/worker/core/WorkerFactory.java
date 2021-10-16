package com.kemalbeyaz.worker.core;

import com.kemalbeyaz.shared.dto.ServiceType;
import com.kemalbeyaz.shared.dto.TaskData;
import com.kemalbeyaz.worker.mysql.MySQLWorker;
import com.kemalbeyaz.worker.redis.RedisWorker;

public class WorkerFactory {

    public static Worker getInstance(final TaskData taskData, final RedisManager redisManager) {
        if (taskData.getServiceType().equals(ServiceType.REDIS)) {
            return new RedisWorker(taskData, redisManager);
        } else if (taskData.getServiceType().equals(ServiceType.MYSQL)) {
            return new MySQLWorker(taskData, redisManager);
        }

        return new LazyWorker(taskData, redisManager);
    }

    private WorkerFactory() {
    }
}
