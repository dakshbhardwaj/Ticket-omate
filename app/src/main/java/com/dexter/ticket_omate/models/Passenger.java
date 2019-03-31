package com.dexter.ticket_omate.models;

public class Passenger {
    private String mName;
    private String mBeaconId;
    private String mStartStationId;
    private String mEndStationId;
    private String mStatus;

    public Passenger(String name, String beaconId, String startStationId, String endStationId,String status) {
        mName = name;
        mBeaconId = beaconId;
        mStartStationId = startStationId;
        mEndStationId = endStationId;
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
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
