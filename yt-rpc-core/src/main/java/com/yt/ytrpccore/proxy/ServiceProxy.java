package com.yt.ytrpccore.proxy;

import cn.hutool.core.collection.CollUtil;
import com.yt.ytrpccore.RpcApplication;
import com.yt.ytrpccore.config.RpcConfig;
import com.yt.ytrpccore.constant.RpcConstant;
import com.yt.ytrpccore.fault.retry.RetryStrategy;
import com.yt.ytrpccore.fault.retry.RetryStrategyFactory;
import com.yt.ytrpccore.fault.tolerant.TolerantStrategy;
import com.yt.ytrpccore.fault.tolerant.TolerantStrategyFactory;
import com.yt.ytrpccore.loadbalancer.LoadBalancer;
import com.yt.ytrpccore.loadbalancer.LoadBalancerFactory;
import com.yt.ytrpccore.model.RpcRequest;
import com.yt.ytrpccore.model.RpcResponse;
import com.yt.ytrpccore.model.ServiceMetaInfo;
import com.yt.ytrpccore.registry.Registry;
import com.yt.ytrpccore.registry.RegistryFactory;
import com.yt.ytrpccore.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理（jdk动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes()).args(args).build();

        try {
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos)) {
                throw new RuntimeException("暂无服务地址");
            }
            // 暂时先取第一个
            // ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0);
            // 改为通过负载均衡器
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfos);

            // 发送TCP请求
            // RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);

            // 发送TCP请求 -> 修改为使用重试机制
            RpcResponse rpcResponse = null;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo));
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }

            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        }
    }
}
