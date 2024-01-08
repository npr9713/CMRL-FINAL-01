package com.example.cmrlproject;

public class FaultItem {
    private String ackno;
    private String status;
    private String device;
    private String description;
    private String time;
    private String station;

    public FaultItem(String ackno, String status, String device, String description, String time, String station) {
        this.ackno = ackno;
        this.status = status;
        this.device = device;
        this.description = description;
        this.time = time;
        this.station = station;
    }

    public String getAckno() {
        return ackno;
    }

    public String getStatus() {
        return status;
    }

    public String getDevice() {
        return device;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getStation() {
        return station;
    }

    @Override
    public String toString() {
        return "Ackno: " + ackno + ", Status: " + status + ", Device: " + device;
    }
}
