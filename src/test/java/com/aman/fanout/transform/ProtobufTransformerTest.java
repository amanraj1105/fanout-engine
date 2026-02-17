package com.aman.fanout.transform;

import com.aman.fanout.model.Record;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProtobufTransformerTest {

    @Test
    void shouldTransformToProtobufBytes() {

        ProtobufTransformer transformer = new ProtobufTransformer();
        Record record = new Record(3, "Charlie", 75);

        byte[] result = transformer.transform(record);

        String decoded = new String(result);

        assertTrue(decoded.contains("PROTOBUF|3|Charlie|75"));
    }
}