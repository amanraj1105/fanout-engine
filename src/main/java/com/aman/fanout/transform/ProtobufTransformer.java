package com.aman.fanout.transform;

import com.aman.fanout.model.Record;

public class ProtobufTransformer implements Transformer<byte[]> {

    @Override
    public byte[] transform(Record record) {
        // Simulated Protobuf encoding
        String simulated = "PROTOBUF|" + record.id() + "|" + record.name() + "|" + record.score();
        return simulated.getBytes();
    }
}