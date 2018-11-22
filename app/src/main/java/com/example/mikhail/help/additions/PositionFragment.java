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
import com.example.mikhail.help.util.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.SphericalUtil;

public class PositionFragment extends Fragment implements OnMapReadyCallback {

    public static final String
            KEY_LATITUDE = "lat",
            KEY_LONGITUDE = "lng",
            KEY_RADIUS = "r";
    public static final int MAX_RADIUS = 110;
    private static final String TAG = "PositionFragment";
    private GoogleMap mMap;
    private Double latitude, longitude, radius;
    private LatLng zeroLocation;
    private OnPositionFragmentDataListener mListener;

    public PositionFragment() {

    }

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

        mListener.OnSendPosition(zeroLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style)));
        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.addCircle(new CircleOptions().center(zeroLocation).fillColor(Color.argb(80, 0, 0, 0)).strokeWidth(10).strokeColor(Color.rgb(149, 149, 149)).radius(MAX_RADIUS));

        GroundOverlayOptions overlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromBitmap(Utilities.getBitmapFromVectorDrawable(this.getContext(), R.drawable.location_bg))).position(zeroLocation, 15f, 15f);

        final GroundOverlay overlay = mMap.addGroundOverlay(overlayOptions);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (SphericalUtil.computeDistanceBetween(zeroLocation, latLng) > MAX_RADIUS)
                    overlay.setPosition(SphericalUtil.computeOffset(zeroLocation, MAX_RADIUS, SphericalUtil.computeHeading(zeroLocation, latLng)));
                else
                    overlay.setPosition(latLng);
                mListener.OnSendPosition(overlay.getPosition());
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zeroLocation, 17f));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPositionFragmentDataListener) {
            mListener = (OnPositionFragmentDataListener) context;
        }
    }

    public interface OnPositionFragmentDataListener {
        void OnSendPosition(LatLng position);
    }
}
