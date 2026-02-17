package com.aman.fanout.transform;

import com.aman.fanout.model.Record;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XmlTransformerTest {

    @Test
    void shouldTransformToXml() {

        XmlTransformer transformer = new XmlTransformer();
        Record record = new Record(2, "Bob", 80);

        String result = transformer.transform(record);

        assertTrue(result.contains("<id>2</id>"));
        assertTrue(result.contains("<name>Bob</name>"));
        assertTrue(result.contains("<score>80</score>"));
    }
}