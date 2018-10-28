package com.example.mikhail.help.additions;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

public class SizeFragment extends Fragment implements OnMapReadyCallback {


    public SizeFragment() {

    }

    private static final String TAG = "Gestures";

    public static final String
            KEY_MAX_RADIUS = "max_radius",
            KEY_MIN_RADIUS = "min_radius",
            KEY_LATITUDE = "lat",
            KEY_LONGITUDE = "lng";

    private TextView size;
    private String strMeters;
    private int currRadius, maxHeight, maxRadius, minRadius;
    private LatLng zeroLocation;
    private GoogleMap mMap;
    private Double latitude, longitude;

    private void getBundles() {
        latitude = getArguments().getDouble(KEY_LATITUDE);
        longitude = getArguments().getDouble(KEY_LONGITUDE);
        maxRadius = getArguments().getInt(KEY_MAX_RADIUS);
        minRadius = getArguments().getInt(KEY_MIN_RADIUS);

        currRadius = minRadius;

        zeroLocation = new LatLng(latitude, longitude);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_size, container, false);

        getBundles();

        size = v.findViewById(R.id.size);

        strMeters = getResources().getString(R.string.meters);

        size.setText(currRadius + " " + strMeters);

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                maxHeight = v.getHeight();

            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {

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
                mListener.OnSendSize(currRadius);

                return true;
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style)));
        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.addCircle(new CircleOptions().center(zeroLocation).fillColor(Color.argb(80, 0, 0, 0)).strokeWidth(10).strokeColor(Color.rgb(149, 149, 149)).radius(80));
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