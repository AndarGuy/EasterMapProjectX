package com.example.mikhail.help.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlay;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FocusedPlace extends Place {

    private GroundOverlay overlay;
    private ArrayList<Place> hidedPlaces = new ArrayList<>();
    private String name, description, imagePath;
    private Bitmap image;

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

    public String getImagePath() {
        return imagePath;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image, Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "LOCATION_IMAGE_" + timeStamp;
        File storageDir = context.getObbDir();
        String path = storageDir.getAbsolutePath() + "/" + imageFileName;
        Utilities.saveBitmap(image, path);
        this.imagePath = path;
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
