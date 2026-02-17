package com.aman.fanout.sinks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WideColumnSink implements Sink<Map<String, Object>> {

    @Override
    public CompletableFuture<Boolean> send(Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency();
            return true;
        });
    }

    @Override
    public String name() {
        return "WideColumnDB";
    }

    private void simulateLatency() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException ignored) {}
    }
}