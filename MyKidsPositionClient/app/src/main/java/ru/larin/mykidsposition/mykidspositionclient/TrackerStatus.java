package ru.larin.mykidsposition.mykidspositionclient;

import android.location.Location;

public class TrackerStatus {
    private int batteryLevel;
    private Location lastLocation;

    public TrackerStatus() {
    }

    public TrackerStatus(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
