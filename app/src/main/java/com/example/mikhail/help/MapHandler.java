package com.example.mikhail.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapHandler implements OnMapReadyCallback {

    public MapHandler(Context context) {
        this.context = context;
    }

    private static final String TAG = "MapHandler";
    private static final float DEFAULT_ZOOM = 15f;

    private static final String ANIMATED_MOVE = "Animated";
    private static final String DEFAULT_MOVE = "Default";

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context context;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapSetup();

        getUserLastLocation();

    }

    private void getUserLastLocation() {
        Log.d(TAG, "getUserLocation: calls");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserLastLocation: task starting");
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Log.d(TAG, "onComplete: task completed");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: task is succesful");
                        Location location = task.getResult();
                        if (location != null) {
                            Log.d(TAG, "onSuccess: location find");
                            onUserLocationFind(location);
                        } else {
                            Log.e(TAG, "onSuccess: location is null");
                            //get current location
                        }
                    }
                    else {
                        Log.e(TAG, "onComplete: task is failtured");
                        //to be or not to be ...
                    }
                }
            });
        }
        else {
            Log.e(TAG, "getUserLastLocation: has not permissions");
            //do something
        }
    }

    private void onUserLocationFind(Location location) {
        moveCameraToLocation(location, DEFAULT_ZOOM, DEFAULT_MOVE);
    }

    private void mapSetup() {
        Log.d(TAG, "mapSetup: setup starting");
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.setMapStyle(new MapStyleOptions(context.getResources().getString(R.string.map_style)));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        Log.d(TAG, "mapSetup: setup complete");
    }

    GoogleMap.OnMyLocationClickListener onMyLocationClickListener = new GoogleMap.OnMyLocationClickListener() {
        @Override
        public void onMyLocationClick(@NonNull Location location) {
            float currentZoom = mMap.getCameraPosition().zoom;
            if (currentZoom < DEFAULT_ZOOM) moveCameraToLocation(location, DEFAULT_ZOOM, ANIMATED_MOVE);
            else moveCameraToLocation(location, currentZoom, ANIMATED_MOVE);
        }
    };

    private void moveCameraToLocation(Location location, float zoom, String move) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + location.getLatitude() + ", lng: " + location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        switch (move) {
            case ANIMATED_MOVE:
                mMap.animateCamera(cameraUpdate);
                break;
            case DEFAULT_MOVE:
                mMap.moveCamera(cameraUpdate);
                break;
        }
    }

}
