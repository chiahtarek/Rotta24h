package com.example.rotta.enums;

public enum HelpRequestStatus {
    ACCEPTED("ACCEPTED"),
    OPEN("OPEN"), 
    ARRIVED("ARRIVED"),
    CANCELLED("CANCELLED"); 

    private String status;

    HelpRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
