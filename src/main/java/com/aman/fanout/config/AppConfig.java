package com.aman.fanout.config;

import java.util.Map;

public class AppConfig {

    public FileConfig file;
    public BufferConfig buffer;
    public Map<String, SinkConfig> sinks;

    public static class FileConfig {
        public String path;
    }

    public static class BufferConfig {
        public int capacity;
    }

    public static class SinkConfig {
        public boolean enabled;
        public int rateLimit;
    }
}
