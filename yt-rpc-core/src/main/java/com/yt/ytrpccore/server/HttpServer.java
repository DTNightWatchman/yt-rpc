package com.yt.ytrpccore.server;

/**
 * @author by Ricardo
 * @Classname HttpServer
 * @Description HttpServer
 * @Date 2024/6/27 17:27
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    void doStart(int port);
}
