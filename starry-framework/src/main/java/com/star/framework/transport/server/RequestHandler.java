package com.star.framework.transport.server;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.ResponseCode;
import com.star.common.extension.ExtensionLoader;
import com.star.framework.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * request进行过程调用的处理器
 *
 * @Author: zzStar
 * @Date: 05-28-2021 10:01
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("serviceProvider");
    }

    /**
     * 处理请求
     *
     * @param request
     * @return
     */
    public Object handle(StarryRequest request) {
        Object service = serviceProvider.getService(request.getInterfaceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(StarryRequest request, Object service) {
        Object res;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            res = method.invoke(service, request.getParameters());
            logger.info("服务: {} 成功调用: {} ", request.getInterfaceName(), request.getMethodName());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.error("未找到指定方法");
            return StarryResponse.fail(ResponseCode.METHOD_NOT_FOUND, request.getRequestId());
        }
        return res;
    }


}
