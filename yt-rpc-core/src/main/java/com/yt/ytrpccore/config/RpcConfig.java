package com.yt.ytrpccore.config;

import com.yt.ytrpccore.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "yt-rpc";

    private String version = "1.0";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 配置中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
}
