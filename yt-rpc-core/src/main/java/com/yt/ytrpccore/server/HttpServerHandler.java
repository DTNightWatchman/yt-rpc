package com.yt.ytrpccore.server;


import com.yt.ytrpccore.RpcApplication;
import com.yt.ytrpccore.model.RpcRequest;
import com.yt.ytrpccore.model.RpcResponse;
import com.yt.ytrpccore.register.LocalRegister;
import com.yt.ytrpccore.serializer.Serializer;
import com.yt.ytrpccore.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 1. 反序列化请求为对象，并从对象请求中获取到参数
 * 2. 根据服务名称从本地注册器中获取到对应的服务实现类
 * 3. 通过反射机制调用方法，得到返回结果
 * 4. 对返回结果进行封装和序列化，并写入到响应中
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        // final Serializer serializer = new JdkSerializer();
        // 修改为通过读取配置文件的 SPI 模式
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        System.out.println("Received request:" + httpServerRequest.method() + " " + httpServerRequest.uri());

        // 异步处理 HTTP 请求
        httpServerRequest.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpc request is null");
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }

            try {
                // 获取到需要调用的服务的实现类，通过反射进行调用
                Class<?> implClass = LocalRegister.get(rpcRequest.getServiceName());
                // 获取服务中的方法(通过方法名和对应的方法参数)
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());

                // 反射调用方法
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应结果
            doResponse(httpServerRequest, rpcResponse, serializer);
        });

    }


    /**
     * 响应结果
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
