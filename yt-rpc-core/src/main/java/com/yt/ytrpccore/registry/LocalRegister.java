package com.yt.ytrpccore.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by Ricardo
 * @Classname LocalRegister
 * @Description 本地服务注册器
 * @Date 2024/6/28 10:19
 */
public class LocalRegister {

    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();


    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }


    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    public static void remove(String serviceName) {
        map.remove(serviceName);
    }


}
