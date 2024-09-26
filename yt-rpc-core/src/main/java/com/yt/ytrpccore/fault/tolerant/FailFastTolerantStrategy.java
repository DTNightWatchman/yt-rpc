package com.yt.ytrpccore.fault.tolerant;

import com.yt.ytrpccore.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 - 容错策略（立即通知调用方）
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
