package com.yt.ytrpccore.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name = "yt-rpc";

    private String version = "1.0";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;
}
