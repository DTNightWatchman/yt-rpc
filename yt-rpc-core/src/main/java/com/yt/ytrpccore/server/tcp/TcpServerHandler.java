package com.yt.ytrpccore.server.tcp;

import com.yt.ytrpccore.model.RpcRequest;
import com.yt.ytrpccore.model.RpcResponse;
import com.yt.ytrpccore.protocol.*;
import com.yt.ytrpccore.registry.LocalRegister;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP 请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            ProtocolMessage<RpcRequest> protocolMessage = null;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse rpcResponse = new RpcResponse();

            if (rpcRequest == null) {
                rpcResponse.setMessage("rpc request is null");
                doBadResponse(netSocket, rpcResponse);
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

            // 发送响应和编码
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);

            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });

        netSocket.handler(bufferHandlerWrapper);
    }

    /**
     * 错误请求响应
     * @param netSocket
     * @param rpcResponse
     */
    private void doBadResponse(NetSocket netSocket, RpcResponse rpcResponse) {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
        header.setType((byte) ProtocolMessageStatusEnum.BAD_REQUEST.getValue());
        ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
        try {
            Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
            netSocket.write(encode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
