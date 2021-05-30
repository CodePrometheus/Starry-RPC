package com.star.server;

import com.star.common.enums.RpcConfig;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.annotation.StarryServiceServerScan;
import com.star.framework.transport.server.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;

/**
 * @Author: zzStar
 * @Date: 05-30-2021 15:15
 */
@StarryServiceServerScan(basePackage = "com.star.server.impl")
public class PackageScannerTest {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(PackageScannerTest.class);
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        String serverType = properties.getProperty("server.type");
        RpcServer bean = (RpcServer) context.getBean(serverType);
        bean.start();
    }
}
