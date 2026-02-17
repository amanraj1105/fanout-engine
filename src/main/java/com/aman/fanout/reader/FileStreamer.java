package com.aman.fanout.reader;

import com.aman.fanout.model.Record;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class FileStreamer {

    private final ObjectMapper mapper = new ObjectMapper();

    public long stream(String filePath,
                       BlockingQueue<Record> queue,
                       AtomicLong totalCounter) throws Exception {

        long count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                Record record = mapper.readValue(line, Record.class);

                queue.put(record);
                count++;
                totalCounter.incrementAndGet();
            }
        }

        return count;
    }
}