package com.yt.ytrpccore.fault.retry;

import com.github.rholder.retry.*;
import com.yt.ytrpccore.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试 - 重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 使用 retryIfExceptionOfType 方法指定当出现 Exception 异常时重试。
                .retryIfExceptionOfType(Exception.class)
                // 使用 withWaitStrategy 方法指定策略，选择 fixedWait 固定时间间隔策略。
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 使用 withStopStrategy 方法指定策略，选择 stopAfterAttempt 超过最大重试次数停止。
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 使用 withRetryListener 监听重试，每次重试时，除了再次执行任务外，还能够打印当前的重试次数。
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
