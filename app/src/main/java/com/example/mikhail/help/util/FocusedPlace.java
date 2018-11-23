package com.example.mikhail.help.util;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.GroundOverlay;

import java.util.ArrayList;

public class FocusedPlace extends Place {

    private Bitmap image;
    private GroundOverlay overlay;
    private ArrayList<Place> hidedPlaces = new ArrayList<>();
    private String name, description;

    public FocusedPlace(Place place) {
        super(place.getId(), place.getType(), place.getLatitude(), place.getLongitude(), place.getIcon(), place.getMarker());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void removeOverlay() {
        this.overlay.remove();
    }

    public void showPlaces() {
        for (Place p : this.hidedPlaces) {
            p.getMarker().setVisible(true);
        }
    }

    public void addHidedPlace(Place place) {
        this.hidedPlaces.add(place);
        place.getMarker().setVisible(false);
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public GroundOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(GroundOverlay overlay) {
        this.overlay = overlay;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
