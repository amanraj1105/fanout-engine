package com.aman.fanout.sinks;

import com.aman.fanout.transform.*;

public class SinkFactory {

    public static RestSink createRestSink() {
        return new RestSink();
    }

    public static GrpcSink createGrpcSink() {
        return new GrpcSink();
    }

    public static MqSink createMqSink() {
        return new MqSink();
    }

    public static WideColumnSink createWideColumnSink() {
        return new WideColumnSink();
    }

    public static JsonTransformer jsonTransformer() {
        return new JsonTransformer();
    }

    public static ProtobufTransformer protobufTransformer() {
        return new ProtobufTransformer();
    }

    public static XmlTransformer xmlTransformer() {
        return new XmlTransformer();
    }

    public static AvroTransformer avroTransformer() {
        return new AvroTransformer();
    }
}