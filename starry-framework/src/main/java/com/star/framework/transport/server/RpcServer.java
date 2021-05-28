package com.star.framework.transport.server;

/**
 * 服务端通用接口
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:59
 */
public interface RpcServer {

    /**
     * 服务端启动
     */
    void start();

    /**
     * 发布
     *
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void publishService(T service, String serviceName);
}
