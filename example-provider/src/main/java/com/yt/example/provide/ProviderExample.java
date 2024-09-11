package com.yt.example.provide;

import com.yt.example.common.service.UserService;
import com.yt.ytrpccore.RpcApplication;
import com.yt.ytrpccore.config.RegistryConfig;
import com.yt.ytrpccore.config.RpcConfig;
import com.yt.ytrpccore.model.ServiceMetaInfo;
import com.yt.ytrpccore.registry.LocalRegister;
import com.yt.ytrpccore.registry.Registry;
import com.yt.ytrpccore.registry.RegistryFactory;
import com.yt.ytrpccore.server.HttpServer;
import com.yt.ytrpccore.server.VertxHttpServer;

/**
 * 服务提供者
 */
public class ProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();

        // 服务注册
        String serviceName = UserService.class.getName();
        LocalRegister.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
