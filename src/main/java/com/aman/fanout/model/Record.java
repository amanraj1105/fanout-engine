package com.aman.fanout.model;

public record Record(int id, String name, int score) {

    public static final Record POISON =
            new Record(-1, "POISON", -1);
}