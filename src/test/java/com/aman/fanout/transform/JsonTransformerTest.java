package com.aman.fanout.transform;

import com.aman.fanout.model.Record;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformerTest {

    @Test
    void shouldTransformToJson() throws Exception {

        JsonTransformer transformer = new JsonTransformer();
        Record record = new Record(1, "Alice", 90);

        String result = transformer.transform(record);

        assertTrue(result.contains("\"id\":1"));
        assertTrue(result.contains("\"name\":\"Alice\""));
        assertTrue(result.contains("\"score\":90"));
    }
}