package com.star.framework.serialization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zzStar
 * @Date: 2021/8/31
 * @Description:
 */
public class KryoThreadLocalConfig implements RejectedExecutionHandler {

    private static final Logger log = LoggerFactory.getLogger(KryoThreadLocalConfig.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            // 尝试向阻塞队列添加
            if (!executor.getQueue().offer(r, 60, TimeUnit.SECONDS)) {
                throw new RejectedExecutionException("Timed Out while attempting to enqueue Task.");
            }
        } catch (Exception e) {
            String msg = String.format("Thread pool is EXHAUSTED!" +
                            " Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                            " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                    executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(),
                    executor.getTaskCount(), executor.getCompletedTaskCount()
                    , executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
            log.warn(msg);
            throw new RejectedExecutionException(msg);
        }
    }

}
