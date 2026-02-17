package com.aman.fanout;

import com.aman.fanout.config.*;
import com.aman.fanout.metrics.*;
import com.aman.fanout.model.Record;
import com.aman.fanout.orchestrator.FanOutOrchestrator;
import com.aman.fanout.reader.FileStreamer;
import com.aman.fanout.sinks.*;
import com.aman.fanout.transform.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting Fan-Out Engine...");

        // Load configuration
        AppConfig config = ConfigLoader.load("application.yaml");

        // Create bounded queue (Backpressure)
        BlockingQueue<Record> queue =
                new ArrayBlockingQueue<>(config.buffer.capacity);

        // Metrics
        MetricsTracker metrics = new MetricsTracker();
        MetricsReporter reporter = new MetricsReporter(metrics);
        reporter.start();

        // Create sink bindings dynamically
        List<FanOutOrchestrator.SinkBinding<?>> bindings = new ArrayList<>();

        if (config.sinks.get("rest").enabled) {
            bindings.add(new FanOutOrchestrator.SinkBinding<>(
                    new ResilientSink<>(new RestSink(), config.sinks.get("rest").rateLimit),
                    new JsonTransformer()
            ));
        }

        if (config.sinks.get("grpc").enabled) {
            bindings.add(new FanOutOrchestrator.SinkBinding<>(
                    new ResilientSink<>(new GrpcSink(), config.sinks.get("grpc").rateLimit),
                    new ProtobufTransformer()
            ));
        }

        if (config.sinks.get("mq").enabled) {
            bindings.add(new FanOutOrchestrator.SinkBinding<>(
                    new ResilientSink<>(new MqSink(), config.sinks.get("mq").rateLimit),
                    new XmlTransformer()
            ));
        }

        if (config.sinks.get("db").enabled) {
            bindings.add(new FanOutOrchestrator.SinkBinding<>(
                    new ResilientSink<>(new WideColumnSink(), config.sinks.get("db").rateLimit),
                    new AvroTransformer()
            ));
        }

        // Start orchestrator
        FanOutOrchestrator orchestrator =
                new FanOutOrchestrator(queue, bindings, metrics);

        orchestrator.start();

        // Track total records read
        AtomicLong totalCounter = new AtomicLong();
        FileStreamer streamer = new FileStreamer();

        long totalRecords =
                streamer.stream(config.file.path, queue, totalCounter);

        metrics.setExpectedTotal(totalRecords);

        // Signal completion to consumer
        queue.put(Record.POISON);

        // Wait for all in-flight tasks to complete
        orchestrator.awaitCompletion();

        // Shutdown executor
        orchestrator.shutdown();

        // Final summary
        System.out.println("\n==== FINAL SUMMARY ====");
        System.out.println("Total Records Read: " + totalRecords);
        System.out.println("Total Processed: " + metrics.getTotalProcessed());
        System.out.println("Success: " + metrics.getTotalSuccess());
        System.out.println("Failure: " + metrics.getTotalFailure());
        System.out.printf("Final Throughput: %.2f records/sec%n",
                metrics.getThroughputPerSecond());
        System.out.println("=======================\n");

        System.exit(0);
    }
}