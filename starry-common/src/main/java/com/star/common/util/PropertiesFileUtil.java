package com.star.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载配置文件
 *
 * @Author: zzStar
 * @Date: 05-28-2021 10:53
 */
public class PropertiesFileUtil {

    private static ConcurrentHashMap<String, Properties> map = new ConcurrentHashMap<>();

    /**
     * 单例
     */
    private PropertiesFileUtil() {
    }

    private static final Logger logger = LoggerFactory.getLogger(PropertiesFileUtil.class);

    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String configPath = "";
        if (url != null) {
            configPath = url.getPath() + fileName;
        }

        if (map.containsKey(configPath)) {
            return map.get(configPath);
        }

        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(configPath), StandardCharsets.UTF_8
        )) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            logger.error("读取配置文件时发生异常: [{}]", fileName);
        }
        map.put(configPath, properties);
        return properties;
    }
}
