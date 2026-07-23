package com.example.rotta.enums;

public enum ProblemType {
    FLATTIRE("flattire"),
    FUEL("fuel"), 
    BATTERY("battery"),
    ENGINE("engine"),
    ACCIDENT("accident"),
    OTHER("other"); 

    private String type;

    ProblemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
