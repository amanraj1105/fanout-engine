package com.aman.fanout.transform;

import com.aman.fanout.model.Record;

import java.util.Map;

public class AvroTransformer implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Record record) {
        return Map.of(
                "id", record.id(),
                "name", record.name(),
                "score", record.score()
        );
    }
}