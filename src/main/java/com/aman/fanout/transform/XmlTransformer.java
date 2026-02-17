package com.aman.fanout.transform;

import com.aman.fanout.model.Record;

public class XmlTransformer implements Transformer<String> {

    @Override
    public String transform(Record record) {
        return "<record>" +
                "<id>" + record.id() + "</id>" +
                "<name>" + record.name() + "</name>" +
                "<score>" + record.score() + "</score>" +
                "</record>";
    }
}