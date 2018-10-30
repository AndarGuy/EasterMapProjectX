package com.example.mikhail.help.additions;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

public class SizeFragment extends Fragment implements OnMapReadyCallback {


    public SizeFragment() {

    }

    private static final String TAG = "Gestures";

    public static final String
            KEY_MAX_RADIUS = "max_radius",
            KEY_MIN_RADIUS = "min_radius";

    private TextView size;
    private String strMeters;
    private int currRadius, maxHeight, maxRadius, minRadius;
    private GoogleMap mMap;
    private Circle circle;
    private FrameLayout touchLayout;
    private LatLng location;

    public void setLocation(LatLng location) {
        try {
            this.location = location;

            circle.setCenter(location);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15F).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.moveCamera(cameraUpdate);
        } catch (NullPointerException e) {

        }
    }

    private void getBundles() {

        maxRadius = getArguments().getInt(KEY_MAX_RADIUS);
        minRadius = getArguments().getInt(KEY_MIN_RADIUS);

        currRadius = (minRadius + maxRadius) / 2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_size, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getBundles();

        mListener.OnSendSize(currRadius);

        size = v.findViewById(R.id.size);

        touchLayout = v.findViewById(R.id.touchLayout);

        strMeters = getResources().getString(R.string.meters);

        size.setText(currRadius + " " + strMeters);

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                maxHeight = v.getHeight();

            }
        });

        touchLayout.setOnTouchListener(new View.OnTouchListener() {

            float startY = 0;
            float startRadius;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                    startRadius = currRadius;
                }

                float currY = event.getY();
                if (currY < 0) currY = 0;

                float diffY = startY - currY;

                currRadius = Math.round(startRadius + (diffY / maxHeight) * maxRadius);

                if (currRadius > maxRadius) currRadius = maxRadius;
                else if (currRadius < minRadius) currRadius = minRadius;

                size.setText(currRadius + " " + strMeters);
                circle.setRadius(currRadius);
                mListener.OnSendSize(currRadius);


                return true;
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style)));

        circle = mMap.addCircle(new CircleOptions().center(new LatLng(0, 0)).fillColor(Color.argb(50, 228, 113, 109)).strokeWidth(10).strokeColor(Color.rgb(228, 113, 109)).radius(currRadius));
    }

    public interface OnSizeFragmentDataListener {
        void OnSendSize(int size);
    }

    private OnSizeFragmentDataListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSizeFragmentDataListener) {
            mListener = (OnSizeFragmentDataListener) context;
        }
    }

}