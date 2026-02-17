package com.aman.fanout.metrics;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MetricsReporter {

    private final MetricsTracker metrics;

    public MetricsReporter(MetricsTracker metrics) {
        this.metrics = metrics;
    }

    public void start() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::report, 5, 5, TimeUnit.SECONDS);
    }

    private void report() {
        System.out.println("==== STATUS UPDATE ====");
        System.out.println("Total Processed: " + metrics.getTotalProcessed());
        System.out.println("Total Success: " + metrics.getTotalSuccess());
        System.out.println("Total Failure: " + metrics.getTotalFailure());

        metrics.getSinkSuccess().forEach((sink, count) ->
                System.out.println("Sink " + sink + " Success: " + count.get())
        );

        metrics.getSinkFailure().forEach((sink, count) ->
                System.out.println("Sink " + sink + " Failure: " + count.get())
        );

        System.out.println("========================\n");
    }
}