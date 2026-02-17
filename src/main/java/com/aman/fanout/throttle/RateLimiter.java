package com.aman.fanout.throttle;

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private final Semaphore permits;
    private final int maxPermits;

    public RateLimiter(int permitsPerSecond) {
        this.maxPermits = permitsPerSecond;
        this.permits = new Semaphore(permitsPerSecond);

        // Refill permits every second
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    int toRelease = maxPermits - permits.availablePermits();
                    if (toRelease > 0) {
                        permits.release(toRelease);
                    }
                }, 1, 1, TimeUnit.SECONDS);
    }

    public void acquire() throws InterruptedException {
        permits.acquire();
    }
}