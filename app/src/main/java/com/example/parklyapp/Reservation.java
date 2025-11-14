package com.example.parklyapp;

public class Reservation {
    private String slotId;
    private String reservedBy;
    private String reservedTime;
    private boolean reservedStatus;

    public Reservation() {}

    public Reservation(String slotId, String reservedBy, String reservedTime, boolean reservedStatus) {
        this.slotId = slotId;
        this.reservedBy = reservedBy;
        this.reservedTime = reservedTime;
        this.reservedStatus = reservedStatus;
    }

    public String getSlotId() { return slotId; }
    public String getReservedBy() { return reservedBy; }
    public String getReservedTime() { return reservedTime; }
    public boolean isReservedStatus() { return reservedStatus; }

    public void setReservedStatus(boolean reservedStatus) {
        this.reservedStatus = reservedStatus;
    }
}