package com.example.mikhail.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class MapHandler implements OnMapReadyCallback {

    private static final String TAG = "MapHandler";
    private static final float DEFAULT_ZOOM = 15f;
    private static final String ANIMATED_MOVE = "Animated";
    private static final String DEFAULT_MOVE = "Default";
    private static final int OK = 0;
    private final String
            IMAGE = "image",
            INFO = "info",
            TYPE = "type",
            ID = "id",
            TO_Y = "to_y",
            TO_X = "to_x",
            FROM_Y = "from_y",
            FROM_X = "from_x",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            PLACE = "place",
            GET = "get";
    private final int[] mThumbIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};
    private final String[] mThumbTypes = {"GR", "MN", "PS", "MO", "CH", "EB", "EG"};
    private final HashMap<String, Integer> iconByType = new HashMap<>();
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
    private HashMap<String, Place> showingPlaces = new HashMap<>();
    private Place focusedPlace;

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
        for (int i = 0; i < mThumbIds.length; i++)
            iconByType.put(mThumbTypes[i], Integer.valueOf(mThumbIds[i]));
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (focusedPlace != null) {
                    focusedPlace.getOverlay().remove();
                    focusedPlace.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(focusedPlace.getIcon()));
                    focusedPlace = null;
                }
            }
        });
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
                        if (result == OK) {
                            Gson gson = new Gson();
                            for (int i = 0; i < response.keySet().size() - 1; i++) {
                                HashMap<String, String> tempPlace = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);
                                Place currentPlace = new Place(tempPlace.get(ID), tempPlace.get(TYPE), Double.valueOf(tempPlace.get(LATITUDE)), Double.valueOf(tempPlace.get(LONGITUDE)));
                                if (!showingPlaces.keySet().contains(currentPlace.getId()) && Arrays.asList(mThumbTypes).contains(currentPlace.getType())) {
                                    Log.d(TAG, "onResponse: Place " + currentPlace.getId() + " added!");
                                    currentPlace.addMarker(mMap, Utilities.tintImage(Utilities.getBitmapFromVectorDrawable(context, iconByType.get(currentPlace.getType())), context.getResources().getColor(R.color.dark_gray)));
                                    currentPlace.getMarker().setAnchor(0.5f, 0.5f);
                                    showingPlaces.put(currentPlace.getId(), currentPlace);
                                }
                            }
                            for (String id : ((HashMap<String, Place>) showingPlaces.clone()).keySet()) {
                                Place place = showingPlaces.get(id);
                                double x = place.getLatitude(), y = place.getLongitude();
                                if (!(x < x1 && x > x2 && y > y1 && y < y2)) {
                                    if (place.equals(focusedPlace)) {
                                        focusedPlace.getOverlay().remove();
                                        focusedPlace = null;
                                    }
                                    place.removeMarker();
                                    showingPlaces.remove(id);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, "onFailure: errorrrrrrrr!!!!" + t.toString() + " " + t.getMessage());
                    }
                });
                request.makeRequest();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                RetrofitRequest request = new RetrofitRequest(PLACE, INFO);
                final Place place = showingPlaces.get(marker.getTitle());

                if (focusedPlace != null) {
                    focusedPlace.getOverlay().remove();
                    focusedPlace.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(focusedPlace.getIcon()));
                    focusedPlace = place;
                } else {
                    focusedPlace = place;
                }

                GroundOverlayOptions overlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromBitmap(Utilities.getBitmapFromVectorDrawable(context, R.drawable.place_on_map_loading_bg))).position(place.getLocation(), 0);
                place.setOverlay(mMap.addGroundOverlay(overlayOptions));
                //place.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(Utilities.tintImage(place.getIcon(), context.getResources().getColor(R.color.white))));
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLng nearRight = visibleRegion.nearRight, farLeft = visibleRegion.farLeft;
                Double x1 = farLeft.latitude, x2 = nearRight.latitude;
                placeOpen(Double.valueOf(x1 - x2).floatValue(), place);

                request.putParam(ID, place.getId());
                request.setListener(new RequestListener() {
                    @Override
                    public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                        if (result == OK) {

                            Bitmap image = Utilities.decodeBase64(response.get(IMAGE));
                            Bitmap crop;
                            if (image.getHeight() > image.getWidth())
                                crop = Bitmap.createBitmap(image, 0, image.getHeight() / 2 - image.getWidth() / 2, image.getWidth(), image.getWidth());
                            else
                                crop = Bitmap.createBitmap(image, image.getWidth() / 2 - image.getHeight() / 2, 0, image.getHeight(), image.getHeight());
                            Bitmap scaled = Bitmap.createScaledBitmap(crop, 300, 300, true);
                            Bitmap circled = Utilities.getCircledBitmap(scaled);
                            Bitmap circledWithBorders = Utilities.addBorderToRoundedBitmap(circled, 150, 10, Color.WHITE);
                            place.getOverlay().setImage(BitmapDescriptorFactory.fromBitmap(circledWithBorders));
                            focusedPlace = place;
                        } else {
                            onFailure(call, new Exception("ERROR: " + result));
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                    }
                });
                request.makeRequest();
                return true;
            }
        });
        mMap.setMapStyle(new MapStyleOptions(context.getResources().getString(R.string.map_style)));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
    }

    private void placeOpen(final float needDistance, final Place place) {

        final int startColor = 77;
        final int endAlpha = 100, startAlpha = 255;
        final int animationTime = 75;
        final float distancePerTime = needDistance * 30000f / animationTime;
        final float iconScalePerTime = 1f / animationTime;
        final float tintColorChangePerTime = (255f - startColor) / animationTime;
        final float tintAlphaPerTime = (startAlpha - endAlpha) / animationTime;

        final Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int time = msg.what;
                int iconHeight = place.getIcon().getHeight(), iconWidth = place.getIcon().getWidth();
                int currentTint = startColor + Float.valueOf(tintColorChangePerTime * time).intValue();
                place.getOverlay().setDimensions(time * distancePerTime);
                place.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(Utilities.tintImage(Bitmap.createScaledBitmap(place.getIcon(), Float.valueOf(iconHeight + iconScalePerTime * time * iconHeight).intValue(), Float.valueOf(iconWidth + iconScalePerTime * time * iconWidth).intValue(), true), Color.argb(Float.valueOf(startAlpha - tintAlphaPerTime * time).intValue(), currentTint, currentTint, currentTint))));
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i < animationTime; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myHandler.sendEmptyMessage(i);
                }
            }
        }).start();

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
