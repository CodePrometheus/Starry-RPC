package com.star.framework.serialization.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: zzStar
 * @Date: 2021/8/31
 * @Description:
 */
public class KryoThreadLocalConfig implements RejectedExecutionHandler {

    /**
     * 定义一个拒绝列表
     */
    private final RejectedExecutionHandler[] handlerChain;

    public KryoThreadLocalConfig(RejectedExecutionHandler[] handlerChain) {
        this.handlerChain = Objects.requireNonNull(handlerChain, "handlerChain must not be null");
    }

    public static RejectedExecutionHandler build(List<RejectedExecutionHandler> chain) {
        Objects.requireNonNull(chain, "handlerChain must not be null");
        RejectedExecutionHandler[] handlerChain = chain.toArray(new RejectedExecutionHandler[0]);
        return new KryoThreadLocalConfig(handlerChain);
    }


    /**
     * 当触发拒绝策略时，会将策略链中的rejectedExecution依次执行一遍
     *
     * @param r
     * @param executor
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Arrays.stream(handlerChain).forEach(rejectedExecutionHandler
                -> rejectedExecutionHandler.rejectedExecution(r, executor));
    }

}
