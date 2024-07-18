package com.yt.example.provide;

import com.yt.example.common.service.UserService;
import com.yt.ytrpccore.RpcApplication;
import com.yt.ytrpccore.register.LocalRegister;
import com.yt.ytrpccore.server.HttpServer;
import com.yt.ytrpccore.server.VertxHttpServer;

/**
 * @author by Ricardo
 * @Classname EasyProviderExample
 * @Description TODO
 * @Date 2024/6/27 16:56
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 注册服务
        RpcApplication.init();
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
