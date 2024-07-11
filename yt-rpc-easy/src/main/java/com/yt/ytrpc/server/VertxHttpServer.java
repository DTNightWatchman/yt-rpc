package com.yt.ytrpc.server;

import io.vertx.core.Vertx;

/**
 * @author by Ricardo
 * @Classname VertxHttpServer
 * @Description vertx
 * @Date 2024/6/27 17:29
 */
public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动http服务器并监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Fail to start server: " + result.cause());
            }
        });
    }
}
