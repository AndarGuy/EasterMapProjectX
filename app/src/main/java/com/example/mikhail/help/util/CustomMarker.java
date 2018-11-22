package com.example.mikhail.help.util;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class CustomMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private MarkerOptions marker;
    private Bitmap icon;

    public CustomMarker(LatLng position, Bitmap icon) {
        this.position = position;
        this.icon = icon;
        setMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(icon)));
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public Bitmap getIcon() {
        return this.icon;
    }
}
