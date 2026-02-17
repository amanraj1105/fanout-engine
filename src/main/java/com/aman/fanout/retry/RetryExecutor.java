package com.aman.fanout.retry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RetryExecutor {

    private static final int MAX_RETRIES = 3;

    public static <T> CompletableFuture<Boolean> executeWithRetry(
            Supplier<CompletableFuture<Boolean>> task,
            DeadLetterQueue<T> dlq,
            T record) {

        return attempt(task, dlq, record, 1);
    }

    private static <T> CompletableFuture<Boolean> attempt(
            Supplier<CompletableFuture<Boolean>> task,
            DeadLetterQueue<T> dlq,
            T record,
            int attempt) {

        return task.get().thenCompose(success -> {

            if (success) {
                return CompletableFuture.completedFuture(true);
            }

            if (attempt >= MAX_RETRIES) {
                dlq.add(record);
                return CompletableFuture.completedFuture(false);
            }

            return attempt(task, dlq, record, attempt + 1);
        });
    }
}