package com.aman.fanout.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsTracker {

    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong totalSuccess = new AtomicLong();
    private final AtomicLong totalFailure = new AtomicLong();

    private final Map<String, AtomicLong> sinkSuccess = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> sinkFailure = new ConcurrentHashMap<>();

    // For Zero Data Loss tracking
    private volatile long expectedTotal;

    // For throughput calculation
    private final long startTime = System.currentTimeMillis();

    /* =========================
       Recording Methods
       ========================= */

    public void recordSuccess(String sinkName) {
        totalProcessed.incrementAndGet();
        totalSuccess.incrementAndGet();
        sinkSuccess
                .computeIfAbsent(sinkName, k -> new AtomicLong())
                .incrementAndGet();
    }

    public void recordFailure(String sinkName) {
        totalProcessed.incrementAndGet();
        totalFailure.incrementAndGet();
        sinkFailure
                .computeIfAbsent(sinkName, k -> new AtomicLong())
                .incrementAndGet();
    }

    /* =========================
       Zero Data Loss Support
       ========================= */

    public void setExpectedTotal(long expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    public boolean isComplete() {
        return totalSuccess.get() + totalFailure.get() >= expectedTotal;
    }

    /* =========================
       Getters
       ========================= */

    public long getTotalProcessed() {
        return totalProcessed.get();
    }

    public long getTotalSuccess() {
        return totalSuccess.get();
    }

    public long getTotalFailure() {
        return totalFailure.get();
    }

    public Map<String, AtomicLong> getSinkSuccess() {
        return sinkSuccess;
    }

    public Map<String, AtomicLong> getSinkFailure() {
        return sinkFailure;
    }

    public long getExpectedTotal() {
        return expectedTotal;
    }

    /* =========================
       Throughput Calculation
       ========================= */

    public double getThroughputPerSecond() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        if (elapsedMillis == 0) return 0;
        return (totalProcessed.get() * 1000.0) / elapsedMillis;
    }
}