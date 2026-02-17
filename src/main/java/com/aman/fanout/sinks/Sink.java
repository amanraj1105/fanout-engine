package com.aman.fanout.sinks;

import java.util.concurrent.CompletableFuture;

public interface Sink<T> {

    CompletableFuture<Boolean> send(T data);
    String name();
}