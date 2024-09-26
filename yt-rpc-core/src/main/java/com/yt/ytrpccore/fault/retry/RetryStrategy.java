package com.yt.ytrpccore.fault.retry;

import com.yt.ytrpccore.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试机制接口
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
