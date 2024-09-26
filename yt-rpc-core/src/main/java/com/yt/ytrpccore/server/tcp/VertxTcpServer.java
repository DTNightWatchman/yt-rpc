package com.yt.ytrpccore.server.tcp;

import com.yt.ytrpccore.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;


public class VertxTcpServer implements HttpServer {

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

//         server.connectHandler(new TcpServerHandler());
        server.connectHandler(socket -> {
            // 构造Parser
            RecordParser parser = RecordParser.newFixed(8);
            parser.setOutput(new Handler<Buffer>() {
                int size = -1;

                // 一次性完整读取（头+体）
                Buffer resultBuffer = Buffer.buffer();

                @Override
                public void handle(Buffer buffer) {
                    if (-1 == size) {
                        // 读取消息体的长度
                        size = buffer.getInt(4);
                        parser.fixedSizeMode(size);
                    } else {
                        // 写入头信息到结果
                        resultBuffer.appendBuffer(buffer);
                        System.out.println(resultBuffer.toString());
                        // 重置一轮
                        parser.fixedSizeMode(8);
                        size = -1;
                        resultBuffer = Buffer.buffer();
                    }
                }
            });
            socket.handler(parser);
        });

        // 启动 TCP 服务并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Fail to start server: " + result.cause());
            }
        });
    }
}
