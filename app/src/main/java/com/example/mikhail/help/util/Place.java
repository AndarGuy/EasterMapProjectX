package com.example.mikhail.help.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.DrawableUtils;

import com.example.mikhail.help.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashMap;

public class Place {
    private String id, type, name;
    private Bitmap icon;
    private LatLng location;
    private Double latitude, longitude;
    private Marker marker;

    private final int[] mIconIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};
    private final String[] mIconTypes = {"GR", "MN", "PS", "MO", "CH", "EB", "EG"};


    public Place(String id, String type, Double latitude, Double longitude, Bitmap icon, Marker marker) {
        this.id = id;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude, longitude);
        this.icon = icon;
        this.marker = marker;
    }

    public Place(String id, String type, Double latitude, Double longitude) {
        this.id = id;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude, longitude);
    }

    public Place(Marker marker) {
        this.marker = marker;
        this.id = marker.getTitle();
        this.location = marker.getPosition();
        this.latitude = this.location.latitude;
        this.longitude = this.location.longitude;
    }

    public void addMarker(GoogleMap googleMap, Bitmap icon) {
        this.marker = googleMap.addMarker(new MarkerOptions().position(location).title(id).icon(BitmapDescriptorFactory.fromBitmap(icon)));
        this.icon = icon;
    }

    public void createIcon(Context context) throws IndexOutOfBoundsException {
        this.icon = Utilities.drawableToBitmap(context.getDrawable(mIconIds[Arrays.asList(mIconTypes).indexOf(type)]));
    }

    public void removeMarker() {
        this.marker.remove();
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LatLng getLocation() {
        return location;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
