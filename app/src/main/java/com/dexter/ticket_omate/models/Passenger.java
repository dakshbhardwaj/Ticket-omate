package com.dexter.ticket_omate.models;

public class Passenger {
    private String mName;
    private String mBeaconId;
    private String mStartStationId;
    private String mEndStationId;

    public Passenger(String name, String beaconId, String startStationId, String endStationId) {
        mName = name;
        mBeaconId = beaconId;
        mStartStationId = startStationId;
        mEndStationId = endStationId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBeaconId() {
        return mBeaconId;
    }

    public void setBeaconId(String beaconId) {
        mBeaconId = beaconId;
    }

    public String getStartStationId() {
        return mStartStationId;
    }

    public void setStartStationId(String startStationId) {
        mStartStationId = startStationId;
    }

    public String getEndStationId() {
        return mEndStationId;
    }

    public void setEndStationId(String endStationId) {
        mEndStationId = endStationId;
    }
}
