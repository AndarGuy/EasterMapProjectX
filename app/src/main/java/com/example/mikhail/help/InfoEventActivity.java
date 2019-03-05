package com.example.mikhail.help;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mikhail.help.util.Event;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;

import retrofit2.Call;

public class InfoEventActivity extends AppCompatActivity {

    private static final String TAG = "InfoPlaceActivity";

    private final int OK = 0;

    private final String
            SIZE  = "size",
            STATE = "state",
            ID = "id",
            EVENT = "event",
            INFO = "info",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            ICON = "icon",
            ADDRESS = "address",
            START_DATE = "start_date",
            END_DATE = "end_date",
            DESCRIPTION = "description",
            NAME = "name",
            IMAGE = "image";

    private TextView description, name, date, info;
    private ImageView image;
    private LinearLayout buttonContainer;
    private ProgressBar progressBar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapHandler.isInfoActivityOpen = false;
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapHandler.isInfoActivityOpen = false;
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(null);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);

        Bundle b = getIntent().getExtras();
        final String id = b.getString(ID),
                startDate = b.getString(START_DATE),
                endDate = b.getString(END_DATE);
        final int state = b.getInt(STATE), size = b.getInt(SIZE);
        final double latitude = b.getDouble(LATITUDE), longitude = b.getDouble(LONGITUDE);

        name = findViewById(R.id.nameText);
        description = findViewById(R.id.descriptionText);
        image = findViewById(R.id.myImage);
        date = findViewById(R.id.dateText);
        info = findViewById(R.id.infoEvent);
        buttonContainer = findViewById(R.id.buttonContainer);
        progressBar = findViewById(R.id.loadingIndicator);

        setupToolbar();

        RetrofitRequest request = new RetrofitRequest(EVENT, INFO);

        request.putParam(ID, id);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                if (result == OK) {

                    name.setText(response.get(NAME));
                    description.setText(response.get(DESCRIPTION));
                    image.setImageBitmap(Utilities.decodeBase64(response.get(IMAGE)));
                    date.setText(String.format("%s - %s", startDate, endDate));
                    progressBar.setVisibility(View.INVISIBLE);

                    if (state == Event.STATE_PAST) {
                        Button first = findViewById(R.id.firstButton);
                        first.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightGrey)));
                        first.setEnabled(false);
                        info.setText(R.string.event_finished);
                    } else if (state == Event.STATE_SIMPLE) {
                        Location userLocation = MapHandler.location;
                        if (SphericalUtil.computeDistanceBetween(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), new LatLng(latitude, longitude)) <= size) {

                        }
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

    }
}
