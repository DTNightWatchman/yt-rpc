package com.yt.ytrpccore.server.tcp;

import io.vertx.core.Vertx;

public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connect to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();

                // 发送数据
                socket.write("hello server");

                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("received response from server:" + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
