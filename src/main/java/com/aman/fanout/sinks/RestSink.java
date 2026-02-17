package com.aman.fanout.sinks;

import java.util.concurrent.CompletableFuture;

public class RestSink implements Sink<String> {

    @Override
    public CompletableFuture<Boolean> send(String data) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency();
            return true;
        });
    }

    @Override
    public String name() {
        return "REST";
    }

    private void simulateLatency() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {}
    }
}