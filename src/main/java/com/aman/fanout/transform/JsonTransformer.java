package com.aman.fanout.transform;

import com.aman.fanout.model.Record;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTransformer implements Transformer<String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String transform(Record record) throws Exception {
        return mapper.writeValueAsString(record);
    }
}