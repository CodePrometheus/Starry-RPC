package com.star.common.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * SPI 扩展点
 *
 * @Author: zzStar
 * @Date: 05-27-2021 17:07
 */
public class ExtensionLoader<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String SERVICE_DIRECTORY = "META-INF/starry/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> CLASS_CACHE = new Holder<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 加载扩展
     *
     * @param type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }

        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") should be an interface!");
        }

        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type (" + type + ") should be annotated with @SPI");
        }

        // 首先从缓存里加载，没有的话则创建
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     * 获取扩展类实例
     *
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        Holder<Object> holder = INSTANCE_CACHE.get(name);
        if (holder == null) {
            INSTANCE_CACHE.putIfAbsent(name, new Holder<>());
            holder = INSTANCE_CACHE.get(name);
        }

        // 不存在创建一个单例对象
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        // 加载该接口下的所有实现
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return instance;
    }

    /**
     * 获取接口下的所有实现
     *
     * @return
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // 缓存中取
        Map<String, Class<?>> classes = CLASS_CACHE.get();

        if (classes == null) {
            synchronized (CLASS_CACHE) {
                classes = CLASS_CACHE.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // 读取配置文件，加载类
                    loadDirectory(classes);
                    CLASS_CACHE.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 读取starry下的文件
     *
     * @param extensionClasses
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = SERVICE_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                // 判断是否包含元素
                while (urls.hasMoreElements()) {
                    URL hashUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, hashUrl);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * 加载starry里的文件内容
     *
     * @param extensionClasses
     * @param classLoader
     * @param url
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8))) {
            String line;
            // 一行一行读取配置文件
            while ((line = reader.readLine()) != null) {
                // 忽略注释后面的东西
                final int hash = line.indexOf('#');
                if (hash >= 0) {
                    // 左闭右开
                    line = line.substring(0, hash);
                }

                // 删除字符串的头尾空白符
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int equals = line.indexOf('=');
                        String name = line.substring(0, equals).trim();
                        // =之后
                        String className = line.substring(equals + 1).trim();
                        // 读取配置 key: extension name, value: className
                        if (name.length() > 0 && className.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(className);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}

