package com.aman.fanout.sinks;

import java.util.concurrent.CompletableFuture;

public class MqSink implements Sink<String> {

    @Override
    public CompletableFuture<Boolean> send(String data) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency();
            return true;
        });
    }

    @Override
    public String name() {
        return "MQ";
    }

    private void simulateLatency() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException ignored) {}
    }
}