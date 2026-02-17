package com.aman.fanout.transform;

import com.aman.fanout.model.Record;

public interface Transformer<T> {
    T transform(Record record) throws Exception;
}