package com.example.mikhail.help.util;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Place {
    private String id, name, description, type;
    private Bitmap image, icon;
    private LatLng location;
    private Double latitude, longitude;
    private Marker marker;
    private GroundOverlay overlay;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addMarker(GoogleMap googleMap, Bitmap icon) {
        this.marker = googleMap.addMarker(new MarkerOptions().position(location).title(id).icon(BitmapDescriptorFactory.fromBitmap(icon)));
        this.icon = icon;
    }

    public void removeMarker() {
        this.marker.remove();
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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

    public void setOverlay(GroundOverlay overlay) {
        this.overlay = overlay;
    }

    public GroundOverlay getOverlay() {
        return overlay;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Bitmap getIcon() {
        return icon;
    }
}
