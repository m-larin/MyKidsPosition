package ru.larin.mykidsposition.mykidspositionclient;

import java.math.BigDecimal;
import java.util.Date;

public class PositionData {
    private double lat;
    private double lon;
    private float accuracy;
    private String date;
    private String prov;
    private int batteryLevel;
    private long person;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", accuracy=" + accuracy +
                ", date='" + date + '\'' +
                ", prov='" + prov + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", person=" + person +
                '}';
    }

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }
}
