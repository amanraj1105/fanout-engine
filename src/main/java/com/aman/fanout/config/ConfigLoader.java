package com.aman.fanout.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;

public class ConfigLoader {

    public static AppConfig load(String filePath) throws Exception {

        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return yaml.loadAs(fis, AppConfig.class);
        }
    }
}