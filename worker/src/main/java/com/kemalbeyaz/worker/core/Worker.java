package com.kemalbeyaz.worker.core;

import java.util.concurrent.CountDownLatch;

public interface Worker {

    CountDownLatch runTask();

}
