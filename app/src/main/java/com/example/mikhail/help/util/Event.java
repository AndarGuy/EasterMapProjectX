package com.example.mikhail.help.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class Event {
    public static final int STATE_PAST = 0, STATE_SIMPLE = 1, STATE_FUTURE = 2, STATE_UNKNOWN = -1;
    private String id;
    private Integer size;
    private LatLng location;
    private Double latitude, longitude;
    private Circle circle;
    private Calendar startDate, endDate;
    private int state = STATE_UNKNOWN;

    public Event(String id, Integer size, Calendar startDate, Calendar endDate, Double latitude, Double longitude) {
        this.id = id;
        this.size = size;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude, longitude);
        setState();
    }

    public int getState() {
        return state;
    }

    private void setState() {

        Calendar currentDate = Calendar.getInstance();

        if (startDate.before(currentDate) && endDate.after(currentDate) || startDate.equals(currentDate)) {
            this.state = STATE_SIMPLE;
        } else if (startDate.before(currentDate) && endDate.before(currentDate)) {
            this.state = STATE_PAST;
        } else {
            this.state = STATE_FUTURE;
        }
    }

    public String getId() {
        return id;
    }

    public Integer getSize() {
        return size;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Circle getCircle() {
        return circle;
    }

    public LatLng getLocation() {
        return location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void removeCircle() {
        circle.remove();
    }

    public void addCircle(GoogleMap map, CircleOptions options) {
        this.circle = map.addCircle(options);
    }
}
