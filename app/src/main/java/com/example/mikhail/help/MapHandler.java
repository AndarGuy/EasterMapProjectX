package com.example.mikhail.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class MapHandler implements OnMapReadyCallback {

    private static final String TAG = "MapHandler";
    private static final float DEFAULT_ZOOM = 15f;
    private static final String ANIMATED_MOVE = "Animated";
    private static final String DEFAULT_MOVE = "Default";
    private final String
            TO_Y = "to_y",
            TO_X = "to_x",
            FROM_Y = "from_y",
            FROM_X = "from_x",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            PLACE = "place",
            GET = "get",
            EMAIL = "email",
            PASSWORD = "password";
    public Location location;
    private GoogleMap mMap;
    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener = new GoogleMap.OnMyLocationClickListener() {
        @Override
        public void onMyLocationClick(@NonNull Location location) {
            float currentZoom = mMap.getCameraPosition().zoom;
            if (currentZoom < DEFAULT_ZOOM)
                moveCameraToLocation(location, DEFAULT_ZOOM, ANIMATED_MOVE);
            else moveCameraToLocation(location, currentZoom, ANIMATED_MOVE);
        }
    };
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context context;

    public MapHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapSetup();

        new Thread() {
            @Override
            public void run() {
                getUserLastLocation();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                run();
            }
        }.start();

    }

    public void getUserLastLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            onUserLocationFind(location);
                        } else {
                        }
                    } else {
                        Log.e(TAG, "onComplete: task is failtured");
                        Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_LONG);
                    }
                }
            });
        } else {
        }
    }

    private void onUserLocationFind(Location location) {
        if (this.location == null) moveCameraToLocation(location, DEFAULT_ZOOM, DEFAULT_MOVE);
        this.location = location;
    }

    private void mapSetup() {
        Log.d(TAG, "mapSetup: setup starting");
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLng nearRight = visibleRegion.nearRight, farLeft = visibleRegion.farLeft;
                final Double x1 = farLeft.latitude, y1 = farLeft.longitude, x2 = nearRight.latitude, y2 = nearRight.longitude;
                RetrofitRequest request = new RetrofitRequest(PLACE, GET);
                request.putParam(FROM_X, x1.toString());
                request.putParam(FROM_Y, y1.toString());
                request.putParam(TO_X, x2.toString());
                request.putParam(TO_Y, y2.toString());
                request.setListener(new RequestListener() {
                    @Override
                    public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                        Log.d(TAG, "onResponse: " + response.keySet().toString());
                        Log.d(TAG, "onResponse: FROM_X: " + x1 + " TO_X: " + x2 + " FROM_Y: " + y1 + " TO_Y: " + y2);
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, "onFailure: errorrrrrrrr!!!!" + t.toString() + " " + t.getMessage());
                    }
                });
                request.makeRequest();
            }
        });
        mMap.setMapStyle(new MapStyleOptions(context.getResources().getString(R.string.map_style)));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
    }

    public void goToPlace(String place) {

        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(place, 1);
        } catch (IOException e) {

        }

        if (list.size() > 0) {
            Address address = list.get(0);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(address.getLatitude(), address.getLongitude())).zoom(mMap.getCameraPosition().zoom).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate);
        }
    }

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
