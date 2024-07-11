package com.yt.example.provide;

import com.yt.example.common.service.UserService;
import com.yt.ytrpc.register.LocalRegister;
import com.yt.ytrpc.server.HttpServer;
import com.yt.ytrpc.server.VertxHttpServer;

/**
 * @author by Ricardo
 * @Classname EasyProviderExample
 * @Description TODO
 * @Date 2024/6/27 16:56
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 注册服务
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8088);
    }
}
