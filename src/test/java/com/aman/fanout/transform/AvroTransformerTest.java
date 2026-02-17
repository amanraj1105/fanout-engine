package com.aman.fanout.transform;

import com.aman.fanout.model.Record;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AvroTransformerTest {

    @Test
    void shouldTransformToMap() {

        AvroTransformer transformer = new AvroTransformer();
        Record record = new Record(4, "David", 88);

        Map<String, Object> result = transformer.transform(record);

        assertEquals(4, result.get("id"));
        assertEquals("David", result.get("name"));
        assertEquals(88, result.get("score"));
    }
}