package com.aman.fanout.sinks;

import com.aman.fanout.retry.DeadLetterQueue;
import com.aman.fanout.retry.RetryExecutor;
import com.aman.fanout.throttle.RateLimiter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ResilientSink<T> implements Sink<T> {

    private final Sink<T> delegate;
    private final RateLimiter rateLimiter;
    private final DeadLetterQueue<T> dlq;

    public ResilientSink(Sink<T> delegate, int rateLimit) {
        this.delegate = delegate;
        this.rateLimiter = new RateLimiter(rateLimit);
        this.dlq = new DeadLetterQueue<>();
    }

    @Override
    public CompletableFuture<Boolean> send(T data) {

        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            return CompletableFuture.completedFuture(false);
        }

        Supplier<CompletableFuture<Boolean>> task =
                () -> delegate.send(data);

        return RetryExecutor.executeWithRetry(task, dlq, data);
    }

    @Override
    public String name() {
        return delegate.name();
    }

    public int dlqSize() {
        return dlq.size();
    }
}