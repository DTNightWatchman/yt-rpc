package com.yt.example.consumer;

import com.yt.ytrpccore.config.RpcConfig;
import com.yt.ytrpccore.utils.ConfigUtils;

public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "ytRpc");
        System.out.println(rpcConfig);
    }
}
