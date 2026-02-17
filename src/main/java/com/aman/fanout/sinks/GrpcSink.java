package com.aman.fanout.sinks;

import java.util.concurrent.CompletableFuture;

public class GrpcSink implements Sink<byte[]> {

    @Override
    public CompletableFuture<Boolean> send(byte[] data) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency();
            return true;
        });
    }

    @Override
    public String name() {
        return "gRPC";
    }

    private void simulateLatency() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}
    }
}