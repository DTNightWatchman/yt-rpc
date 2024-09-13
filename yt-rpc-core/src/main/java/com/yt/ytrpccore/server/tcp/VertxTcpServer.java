package com.yt.ytrpccore.server.tcp;

import com.yt.ytrpccore.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;


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

        server.connectHandler(netSocket -> {
            // 处理连接
            netSocket.handler(buffer -> {
                // 处理接收到的字节数组
                byte[] requestData = buffer.getBytes();
                // 在这里进行自定义的字节数组处理逻辑，比如解析请求、调用服务、构造响应等
                byte[] responseData = handleRequest(requestData);
                // 发送响应
                netSocket.write(Buffer.buffer(responseData));
            });
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

    private byte[] handleRequest(byte[] requestData) {
        // todo 这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        System.out.println(new String(requestData));
        // 示例，实际逻辑需要根据具体业务来实现
        return "hello world".getBytes();
    }
}
