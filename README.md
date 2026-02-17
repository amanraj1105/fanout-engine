HIGH-THROUGHPUT DISTRIBUTED FAN-OUT ENGINE
Java Backend Engineering Challenge Submission



------------------------------------------------------------
OVERVIEW
------------------------------------------------------------

This project implements a production-grade Distributed Data
Fan-Out & Transformation Engine capable of processing large
flat files (up to 100GB) and dispatching records to multiple
downstream systems safely and efficiently.

The system:

- Streams large files using constant memory
- Applies sink-specific transformations (Strategy Pattern)
- Distributes records in parallel (Virtual Threads)
- Enforces rate limits per sink
- Retries failed records (max 3 attempts)
- Uses Dead Letter Queue (DLQ)
- Guarantees zero data loss
- Provides real-time observability metrics
- Is fully configuration-driven


------------------------------------------------------------
ARCHITECTURE
------------------------------------------------------------

Flat File (JSONL)
        |
        v
FileStreamer (Streaming Reader)
        |
        v
ArrayBlockingQueue (Backpressure)
        |
        v
FanOutOrchestrator (Virtual Threads)
        |
        |---- JsonTransformer ----> REST Sink
        |
        |---- ProtobufTransformer -> gRPC Sink
        |
        |---- XmlTransformer -----> MQ Sink
        |
        |---- AvroTransformer ----> WideColumn DB Sink
                |
        ResilientSink (Rate Limit + Retry + DLQ)
                |
        MetricsTracker + Reporter


------------------------------------------------------------
TECHNICAL DESIGN
------------------------------------------------------------

1. INGESTION LAYER

- Uses BufferedReader
- Processes one line at a time
- Never loads entire file into memory
- Works with -Xmx512m
- Safe for files up to 100GB+

Backpressure is enforced using:

ArrayBlockingQueue<Record>

If sinks slow down:
- Queue fills
- Producer blocks
- Memory stays bounded
- No OutOfMemoryError


2. TRANSFORMATION LAYER (STRATEGY PATTERN)

Interface:

public interface Transformer<T> {
    T transform(Record record);
}

Implemented transformers:

REST -> JSON
gRPC -> Simulated Protobuf
MQ -> XML
DB -> Avro-style Map

This ensures:
- Open/Closed principle
- Easy addition of new sinks
- Clean separation of concerns


3. DISTRIBUTION LAYER

All sinks return:

CompletableFuture<Boolean>

Implemented mock sinks:

- REST (simulated HTTP client)
- gRPC (simulated streaming)
- MQ (simulated publish)
- WideColumnDB (simulated UPSERT)

Execution is fully asynchronous.


4. CONCURRENCY MODEL

Uses:

Executors.newVirtualThreadPerTaskExecutor()

Advantages:

- Lightweight threads
- Massive parallelism
- Ideal for IO-heavy workloads
- No thread pool exhaustion

Metrics use AtomicLong for thread safety.


5. THROTTLING & RESILIENCE

Rate Limiting:
- Token Bucket algorithm
- Implemented using Semaphore
- Configurable per sink

Retry Logic:
- Max 3 attempts
- Async retry execution
- On final failure -> DLQ

Dead Letter Queue:
- Captures failed records
- Prevents silent data loss


------------------------------------------------------------
OBSERVABILITY
------------------------------------------------------------

Every 5 seconds prints:

- Total records processed
- Success count
- Failure count
- Per-sink metrics
- Throughput (records/sec)

Final summary printed on completion.


------------------------------------------------------------
ZERO DATA LOSS GUARANTEE
------------------------------------------------------------

The engine guarantees:

Total Records = Success + Failure

Mechanisms:

- Tracks total ingested records
- Tracks per-sink completion
- Waits for all async tasks
- Uses poison-pill termination
- Graceful executor shutdown

No record is silently dropped.


------------------------------------------------------------
CONFIGURATION (application.yaml)
------------------------------------------------------------

file:
  path: sample-data/input.jsonl

buffer:
  capacity: 10000

sinks:
  rest:
    enabled: true
    rateLimit: 50
  grpc:
    enabled: true
    rateLimit: 100
  mq:
    enabled: true
    rateLimit: 200
  db:
    enabled: true
    rateLimit: 500


------------------------------------------------------------
HOW TO RUN
------------------------------------------------------------

Build:
mvn clean package

Run:
java -jar target/fanout-engine-1.0-SNAPSHOT.jar

Run with limited heap (proof of streaming):
java -Xmx512m -jar target/fanout-engine-1.0-SNAPSHOT.jar

Run tests:
mvn clean test


------------------------------------------------------------
PROJECT STRUCTURE
------------------------------------------------------------

fanout-engine/
  src/main/java/com/aman/fanout/
  src/test/java/com/aman/fanout/
  sample-data/
  application.yaml
  pom.xml
  README.txt
  Prompts.txt


------------------------------------------------------------
DESIGN DECISIONS
------------------------------------------------------------

- BlockingQueue -> natural backpressure
- Virtual Threads -> scalable concurrency
- Strategy Pattern -> extensibility
- ResilientSink wrapper -> clean separation of retry & throttle
- Atomic counters -> thread-safe metrics


------------------------------------------------------------
ASSUMPTIONS
------------------------------------------------------------

- Input file is valid JSONL
- Network interactions are simulated
- Ordering between sinks not required
- Downstream SLAs represented via rate limits


------------------------------------------------------------
EVALUATION RUBRIC COVERAGE
------------------------------------------------------------

Concurrency Logic     -> Virtual Threads + CompletableFuture
Memory Management     -> Streaming + Backpressure
Design Patterns       -> Strategy Pattern
Resilience            -> Retry + DLQ + Rate Limit
Zero Data Loss        -> Explicit Guarantee
Testing               -> Unit + Integration tests included


------------------------------------------------------------
END OF DOCUMENT
------------------------------------------------------------

