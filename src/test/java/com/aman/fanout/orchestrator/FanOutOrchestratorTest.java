package com.aman.fanout.orchestrator;

import com.aman.fanout.metrics.MetricsTracker;
import com.aman.fanout.model.Record;
import com.aman.fanout.sinks.ResilientSink;
import com.aman.fanout.sinks.Sink;
import com.aman.fanout.transform.Transformer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class FanOutOrchestratorTest {

    static class FakeTransformer implements Transformer<String> {
        @Override
        public String transform(Record record) {
            return "TRANSFORMED";
        }
    }

    static class FakeSink implements Sink<String> {

        @Override
        public CompletableFuture<Boolean> send(String data) {
            return CompletableFuture.completedFuture(true);
        }

        @Override
        public String name() {
            return "FAKE_SINK";
        }
    }

    @Test
void shouldProcessRecordAndUpdateMetrics() throws Exception {

    BlockingQueue<Record> queue = new ArrayBlockingQueue<>(10);
    MetricsTracker metrics = new MetricsTracker();

    ResilientSink<String> resilientSink =
            new ResilientSink<>(new FakeSink(), 100);

    FanOutOrchestrator.SinkBinding<String> binding =
            new FanOutOrchestrator.SinkBinding<>(
                    resilientSink,
                    new FakeTransformer()
            );

    FanOutOrchestrator orchestrator =
            new FanOutOrchestrator(queue, List.of(binding), metrics);

    orchestrator.start();

    queue.put(new Record(1, "Test", 100));
    queue.put(Record.POISON);

    // Wait up to 2 seconds for async processing
    long timeout = System.currentTimeMillis() + 2000;
    while (metrics.getTotalProcessed() == 0 &&
           System.currentTimeMillis() < timeout) {
        Thread.sleep(10);
    }

    orchestrator.shutdown();

    assertEquals(1, metrics.getTotalSuccess());
    assertEquals(1, metrics.getTotalProcessed());
}
}