package com.yt.ytrpccore.register;

import com.yt.ytrpccore.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写入缓存
     * @param newServiceCache
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 读取缓存
     *
     * @return 缓存信息
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清理缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }
}
