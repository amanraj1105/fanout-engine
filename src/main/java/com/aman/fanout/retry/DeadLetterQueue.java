package com.aman.fanout.retry;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DeadLetterQueue<T> {

    private final ConcurrentLinkedQueue<T> failedRecords = new ConcurrentLinkedQueue<>();

    public void add(T record) {
        failedRecords.add(record);
    }

    public int size() {
        return failedRecords.size();
    }
}