package com.example.mikhail.help;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.example.mikhail.help.util.Event;
import com.example.mikhail.help.util.FocusedPlace;
import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class MapHandler implements OnMapReadyCallback {

    private static final String TAG = "MapHandler";
    private static final float DEFAULT_ZOOM = 15f, EVENT_ZOOM = 10f;
    private static final float VIEW_ZONE_OFFSET = 0.5f;
    private static final String ANIMATED_MOVE = "Animated";
    private static final String DEFAULT_MOVE = "Default";
    private static final int OK = 0;
    public static Location location;
    static boolean isInfoActivityOpen;
    private final String
            ICON = "icon",
            STATE = "state",
            EVENT = "event",
            DESCRIPTION = "description",
            NAME = "name",
            IMAGE = "image",
            START_DATE = "start_date",
            END_DATE = "end_date",
            SIZE = "size",
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
    private LocationCallback mLocationCallback;
    private Context context;
    private HashMap<String, Place> showingPlaces = new HashMap<>();
    private HashMap<String, Event> showingEvents = new HashMap<>();
    private FocusedPlace focusedPlace;

    MapHandler(Context context) {
        isInfoActivityOpen = false;
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

    private void getUserLastLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            onUserLocationFind(location);
                        }
                    } else {
                        Log.e(TAG, "onComplete: task is failtured");
                        Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    private void onUserLocationFind(Location location) {
        if (this.location == null) moveCameraToLocation(location, DEFAULT_ZOOM, DEFAULT_MOVE);
        this.location = location;
    }

    private List<Place> checkNearPlaces(Location location) {
        for (Place place : showingPlaces.values()) {
            if (SphericalUtil.computeDistanceBetween(place.getLocation(), new LatLng(location.getLatitude(), location.getLongitude())) < 20) {
                Log.d(TAG, "checkNearPlaces: near_place! " + place.getId());
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private void mapSetup() {
        Log.d(TAG, "mapSetup: setup starting");
        LocationListener mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        };
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationProviderClient.requestLocationUpdates(LocationRequest.create(), new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation().getLatitude() + locationResult.getLastLocation().getLongitude());
            }
        }, null);
        for (int i = 0; i < mThumbIds.length; i++)
            iconByType.put(mThumbTypes[i], mThumbIds[i]);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                unfocusedPlace();
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLng nearRight = visibleRegion.nearRight, farLeft = visibleRegion.farLeft;
                final Double x1 = farLeft.latitude, y1 = farLeft.longitude, x2 = nearRight.latitude, y2 = nearRight.longitude;

                final RetrofitRequest request = new RetrofitRequest(PLACE, GET);
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
                                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                                LatLng nearRight = visibleRegion.nearRight, farLeft = visibleRegion.farLeft;
                                Double x1 = farLeft.latitude, x2 = nearRight.latitude;
                                Double needDistance = (x1 - x2) * 30000f;
                                if (!showingPlaces.keySet().contains(currentPlace.getId()) && Arrays.asList(mThumbTypes).contains(currentPlace.getType())) {
                                    currentPlace.addMarker(mMap, Utilities.tintImage(Utilities.getBitmapFromVectorDrawable(context, iconByType.get(currentPlace.getType())), context.getResources().getColor(R.color.darkGrey)));
                                    currentPlace.getMarker().setAnchor(0.5f, 0.5f);
                                    if (focusedPlace != null && SphericalUtil.computeDistanceBetween(focusedPlace.getLocation(), currentPlace.getLocation()) < needDistance)
                                        focusedPlace.addHidedPlace(currentPlace);
                                    Log.d(TAG, "onResponse: Place " + currentPlace.getId() + " added!");
                                    showingPlaces.put(currentPlace.getId(), currentPlace);
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

                final RetrofitRequest request1 = new RetrofitRequest(EVENT, GET);
                request1.putParam(FROM_X, x1.toString());
                request1.putParam(FROM_Y, y1.toString());
                request1.putParam(TO_X, x2.toString());
                request1.putParam(TO_Y, y2.toString());

                request1.setListener(new RequestListener() {
                    @Override
                    public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                        Log.d(TAG, "onResponse: " + response);
                        if (result == OK) {
                            Gson gson = new Gson();
                            Calendar currentDate = Calendar.getInstance();
                            for (int i = 0; i < response.keySet().size() - 1; i++) {
                                HashMap<String, String> tempEvent = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);

                                String startDateStr = tempEvent.get(START_DATE), endDateStr = tempEvent.get(END_DATE);
                                Calendar startDate = Utilities.parseDateFromString(startDateStr), endDate = Utilities.parseDateFromString(endDateStr);

                                Event currentEvent = new Event(tempEvent.get(ID), Integer.valueOf(tempEvent.get(SIZE)), startDate, endDate, Double.valueOf(tempEvent.get(LATITUDE)), Double.valueOf(tempEvent.get(LONGITUDE)));

                                if (!showingEvents.keySet().contains(currentEvent.getId())) {

                                    int color;

                                    if (currentEvent.getState() == Event.STATE_SIMPLE) {
                                        color = ContextCompat.getColor(context, R.color.colorAccent);
                                    } else if (currentEvent.getState() == Event.STATE_PAST) {
                                        color = ContextCompat.getColor(context, R.color.darkGrey);
                                    } else {
                                        color = ContextCompat.getColor(context, R.color.softBlue);
                                    }

                                    CircleOptions options = new CircleOptions()
                                            .center(currentEvent.getLocation())
                                            .fillColor(Color.argb(50, Color.red(color), Color.green(color), Color.blue(color)))
                                            .strokeWidth(10).strokeColor(color)
                                            .radius(currentEvent.getSize())
                                            .clickable(true);
                                    currentEvent.addCircle(mMap, options);
                                    showingEvents.put(currentEvent.getId(), currentEvent);
                                    Log.d(TAG, "onResponse: Event " + currentEvent.getId() + " added!");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, "onFailure: errorrrrrrrr!!!!" + t.toString() + " " + t.getMessage());
                    }
                });
                if (mMap.getCameraPosition().zoom >= EVENT_ZOOM) {
                    request1.makeRequest();
                }

                double offsetX = (x1 - x2) * VIEW_ZONE_OFFSET, offsetY = (y2 - y1) * VIEW_ZONE_OFFSET;

                for (String id : ((HashMap<String, Place>) showingPlaces.clone()).keySet()) {
                    Place place = showingPlaces.get(id);
                    double x = place.getLatitude(), y = place.getLongitude();
                    if (!(x - offsetX < x1 && x + offsetX > x2 && y + offsetY > y1 && y - offsetY < y2)) {
                        place.removeMarker();
                        showingPlaces.remove(id);
                    }
                }

                for (String id : ((HashMap<String, Event>) showingEvents.clone()).keySet()) {
                    Event event = showingEvents.get(id);
                    double x = event.getLatitude(), y = event.getLongitude();
                    if (!(x - offsetX < x1 && x + offsetX > x2 && y + offsetY > y1 && y - offsetY < y2)) {
                        event.removeCircle();
                        showingEvents.remove(id);
                    }
                }
            }
        });

        mMap.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            @Override
            public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                Log.d(TAG, "onGroundOverlayClick: clicek");
                if (focusedPlace.getImage() != null && focusedPlace.getOverlay().equals(groundOverlay)) {
                    openInfoPlaceActivity();
                }
            }
        });

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                Log.d(TAG, "onCircleClick: clicek");
                for (String id : showingEvents.keySet()) {
                    if (circle.equals(showingEvents.get(id).getCircle())) {
                        if (showingEvents.get(id).getState() == Event.STATE_PAST) {
                            Toast.makeText(context, R.string.event_finished, Toast.LENGTH_SHORT).show();
                        } else {
                            if (!isInfoActivityOpen) {
                                openInfoEventActivity(showingEvents.get(id));
                            }
                        }
                    }
                }
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                unfocusedPlace();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (mMap.getCameraPosition().zoom < 13f) {
                    moveCameraToPosition(marker.getPosition(), 15f);
                    return true;
                }
                if (focusedPlace != null && focusedPlace.getId().equals(marker.getTitle())) {
                    if (!isInfoActivityOpen && focusedPlace.getImage() != null) {
                        openInfoPlaceActivity();
                    }
                    return true;
                }
                RetrofitRequest request = new RetrofitRequest(PLACE, INFO);

                moveCameraToPosition(marker.getPosition(), mMap.getCameraPosition().zoom);

                unfocusedPlace();
                focusedPlace = new FocusedPlace(showingPlaces.get(marker.getTitle()));

                GroundOverlayOptions overlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromBitmap(Utilities.getBitmapFromVectorDrawable(context, R.drawable.place_on_map_loading_bg))).position(focusedPlace.getLocation(), 0).clickable(true);
                focusedPlace.setOverlay(mMap.addGroundOverlay(overlayOptions));
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLng nearRight = visibleRegion.nearRight, farLeft = visibleRegion.farLeft;
                Double x1 = farLeft.latitude, x2 = nearRight.latitude;
                placeOpen(Double.valueOf(x1 - x2).floatValue(), focusedPlace);

                request.putParam(ID, focusedPlace.getId());
                request.setListener(new RequestListener() {
                    @Override
                    public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                        if (result == OK) {
                            Bitmap image = Utilities.decodeBase64(response.get(IMAGE));
                            String name = response.get(NAME), description = response.get(DESCRIPTION);
                            Bitmap crop;
                            if (image.getHeight() > image.getWidth())
                                crop = Bitmap.createBitmap(image, 0, image.getHeight() / 2 - image.getWidth() / 2, image.getWidth(), image.getWidth());
                            else
                                crop = Bitmap.createBitmap(image, image.getWidth() / 2 - image.getHeight() / 2, 0, image.getHeight(), image.getHeight());

                            Bitmap scaled = Bitmap.createScaledBitmap(crop, 300, 300, false);
                            Bitmap circled = Utilities.getCircledBitmap(scaled);
                            Bitmap circledWithBorders = Utilities.addBorderToRoundedBitmap(circled, 150, 15, Color.WHITE);
                            try {
                                focusedPlace.getOverlay().setImage(BitmapDescriptorFactory.fromBitmap(circledWithBorders));
                                focusedPlace.setImage(image, context);
                                focusedPlace.setDescription(description);
                                focusedPlace.setName(name);
                            } catch (Exception e) {
                                unfocusedPlace();
                            }
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

    private void unfocusedPlace() {
        if (focusedPlace != null) {
            if (focusedPlace.getImagePath() != null && !focusedPlace.getImagePath().isEmpty()) {
                new File(focusedPlace.getImagePath()).delete();
            }
            if (focusedPlace.getOverlay() != null)
                focusedPlace.removeOverlay();
            focusedPlace.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(focusedPlace.getIcon()));
            focusedPlace.showPlaces();
            focusedPlace = null;
        }
    }

    public void placeOpen(final float needDistance, final FocusedPlace focusedPlace) {

        focusedPlace.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(focusedPlace.getIcon()));

        final int startColor = 77;
        final int endAlpha = 100, startAlpha = 255;
        final int delayTimeInMills = 15;
        final int animationTimes = 20;
        final float distancePerTime = needDistance * 35000f / animationTimes;
        final float iconScalePerTime = 1f / animationTimes;
        final float tintColorChangePerTime = (255f - startColor) / animationTimes;
        final float tintAlphaPerTime = (startAlpha - endAlpha) / animationTimes;

        @SuppressLint("HandlerLeak") final Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int time = msg.what;
                int iconHeight = focusedPlace.getIcon().getHeight(), iconWidth = focusedPlace.getIcon().getWidth();
                int currentTint = startColor + Float.valueOf(tintColorChangePerTime * time).intValue();
                focusedPlace.getOverlay().setDimensions(time * distancePerTime);
                focusedPlace.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(Utilities.tintImage(Bitmap.createScaledBitmap(focusedPlace.getIcon(), Float.valueOf(iconHeight + iconScalePerTime * time * iconHeight).intValue(), Float.valueOf(iconWidth + iconScalePerTime * time * iconWidth).intValue(), true), Color.argb(Float.valueOf(startAlpha - tintAlphaPerTime * time).intValue(), currentTint, currentTint, currentTint))));
                for (String id : showingPlaces.keySet()) {
                    Place showingPlace = showingPlaces.get(id);
                    if (!focusedPlace.getId().equals(showingPlace.getId()) && SphericalUtil.computeDistanceBetween(focusedPlace.getLocation(), showingPlace.getLocation()) < time * distancePerTime / 2) {
                        focusedPlace.addHidedPlace(showingPlace);
                    }
                }
            }
        };

        Thread thread = (new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 5; i < animationTimes; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(delayTimeInMills);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myHandler.sendEmptyMessage(i);
                }
            }
        }));

        thread.start();

    }

    void goToPlace(String place) {

        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(place, 1);
        } catch (IOException ignored) {

        }

        if (list.size() > 0) {
            Address address = list.get(0);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(address.getLatitude(), address.getLongitude())).zoom(mMap.getCameraPosition().zoom).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void openInfoPlaceActivity() {

        isInfoActivityOpen = true;

        Intent intent = new Intent(context, InfoPlaceActivity.class);

        intent.putExtra(ID, focusedPlace.getId());
        intent.putExtra(NAME, focusedPlace.getName());
        intent.putExtra(DESCRIPTION, focusedPlace.getDescription());
        intent.putExtra(IMAGE, focusedPlace.getImagePath());
        intent.putExtra(ICON, iconByType.get(focusedPlace.getType()));
        intent.putExtra(LATITUDE, focusedPlace.getLatitude());
        intent.putExtra(LONGITUDE, focusedPlace.getLongitude());


        context.startActivity(intent);
    }

    private void openInfoEventActivity(Event event) {

        isInfoActivityOpen = true;

        Intent intent = new Intent(context, InfoEventActivity.class);
        intent.putExtra(ID, event.getId());
        intent.putExtra(STATE, event.getState());
        intent.putExtra(START_DATE, DateFormat.format("d MMMM yyyy, HH:mm", event.getStartDate()));
        intent.putExtra(END_DATE, DateFormat.format("d MMMM yyyy, HH:mm", event.getEndDate()));
        intent.putExtra(SIZE, event.getSize());
        intent.putExtra(LATITUDE, event.getLatitude());
        intent.putExtra(LONGITUDE, event.getLongitude());

        context.startActivity(intent);
    }


    public void moveCameraToPosition(LatLng position, float zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(zoom).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
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
