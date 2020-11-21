package ru.larin.mykidsposition.mykidspositionclient;

public class TrackerConfig {
    private int interval;
    private long person;
    private int distance;
    private Accuracy accuracy;

    public TrackerConfig() {
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Accuracy getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Accuracy accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "TrackerConfig{" +
                "interval=" + interval +
                ", person=" + person +
                ", distance=" + distance +
                ", accuracy=" + accuracy +
                '}';
    }
}
