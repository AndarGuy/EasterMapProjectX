package com.example.mikhail.help.additions;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mikhail.help.R;
import com.example.mikhail.help.things.Utilities;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

public class PositionFragment extends Fragment implements OnMapReadyCallback {

    public PositionFragment() {

    }

    private static final String TAG = "PositionFragment";

    public static final String
            KEY_LATITUDE = "lat",
            KEY_LONGITUDE = "lng",
            KEY_RADIUS = "r";

    private GoogleMap mMap;
    private Double latitude, longitude, radius;
    private LatLng zeroLocation;

    private void getBundles() {
        latitude = getArguments().getDouble(KEY_LATITUDE);
        longitude = getArguments().getDouble(KEY_LONGITUDE);
        radius = getArguments().getDouble(KEY_RADIUS);

        zeroLocation = new LatLng(latitude, longitude);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_position, container, false);

        getBundles();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style)));
        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.addCircle(new CircleOptions().center(zeroLocation).fillColor(Color.argb(80, 0, 0, 0)).strokeWidth(10).strokeColor(Color.rgb(149, 149, 149)).radius(80));

        GroundOverlayOptions overlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromBitmap(Utilities.getBitmapFromVectorDrawable(this.getContext(), R.drawable.location_bg))).position(zeroLocation, 15f, 15f);

        final GroundOverlay overlay = mMap.addGroundOverlay(overlayOptions);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Double x0 = zeroLocation.latitude, y0 = zeroLocation.longitude, x1 = latLng.latitude, y1 = latLng.longitude;
                if (Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)) > 0.001) {
                    double new_x, new_y;
                    if (x1 >= x0) {
                        new_x = ((radius * Math.abs(x1 - x0)) / (Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)))) + x0;
                    } else {
                        new_x = (-1 * (radius * Math.abs(x1 - x0)) / (Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)))) + x0;
                    }
                    if (y1 >= y0) {
                        new_y = ((radius * Math.abs(y1 - y0)) / (Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)))) + y0;
                    } else {
                        new_y = (-1 * (radius * Math.abs(y1 - y0)) / (Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)))) + y0;
                    }
                    Log.d(TAG, "onMapClick: " + Math.sqrt(Math.pow(new_x - x0, 2) + Math.pow(new_y - y0, 2)));

                    overlay.setPosition(new LatLng(new_x, new_y));
                } else overlay.setPosition(latLng);
                mListener.OnSendPosition(overlay.getPosition());
            }
        });


        CameraPosition cameraPosition = new CameraPosition.Builder().target(zeroLocation).zoom(17F).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);

    }


    public interface OnPositionFragmentDataListener {
        void OnSendPosition(LatLng position);
    }

    private OnPositionFragmentDataListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPositionFragmentDataListener) {
            mListener = (OnPositionFragmentDataListener) context;
        }
    }
}
