package com.aman.fanout.orchestrator;

import com.aman.fanout.metrics.MetricsTracker;
import com.aman.fanout.model.Record;
import com.aman.fanout.sinks.ResilientSink;
import com.aman.fanout.transform.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FanOutOrchestrator {

    private final BlockingQueue<Record> queue;
    private final List<SinkBinding<?>> bindings;
    private final MetricsTracker metrics;

    private final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();

    private final List<CompletableFuture<?>> inFlightTasks =
            new CopyOnWriteArrayList<>();

    public FanOutOrchestrator(
            BlockingQueue<Record> queue,
            List<SinkBinding<?>> bindings,
            MetricsTracker metrics) {

        this.queue = queue;
        this.bindings = bindings;
        this.metrics = metrics;
    }

   public void start() {

    executor.submit(() -> {
        try {
            while (true) {

                Record record = queue.take();

                if (record.equals(Record.POISON)) {
                    break;
                }

                processRecord(record);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
        }
    });
}

    private <T> void processRecord(Record record) {

        for (SinkBinding<?> binding : bindings) {

            CompletableFuture<?> future =
                    CompletableFuture.runAsync(() -> {
                        try {
                            processSingleSink(record, (SinkBinding<T>) binding);
                        } catch (Exception e) {
                            metrics.recordFailure(binding.sink.name());
                        }
                    }, executor);

            inFlightTasks.add(future);
        }
    }

    private <T> void processSingleSink(Record record,
                                       SinkBinding<T> binding) throws Exception {

        T transformed = binding.transformer.transform(record);

        CompletableFuture<Boolean> sendFuture =
                binding.sink.send(transformed);

        sendFuture.join(); // Ensure completion

        if (sendFuture.get()) {
            metrics.recordSuccess(binding.sink.name());
        } else {
            metrics.recordFailure(binding.sink.name());
        }
    }

    public void awaitCompletion() {

        CompletableFuture.allOf(
                inFlightTasks.toArray(new CompletableFuture[0])
        ).join();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public record SinkBinding<T>(
            ResilientSink<T> sink,
            Transformer<T> transformer
    ) {}
}