package com.example.mikhail.help;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mikhail.help.util.FocusedPlace;
import com.example.mikhail.help.util.NearAdapter;
import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

public class NearActivity extends AppCompatActivity {

    private static final String TAG = "NearActivity";

    NearAdapter adapter;

    AdapterView.OnItemClickListener gridviewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                FocusedPlace place = adapter.getPlace(position);
                openInfoPlaceActivity(place);
            } catch (Exception ignored) {
                Log.d(TAG, "onItemClick: error");
                ignored.printStackTrace();
            }
        }
    };

    private void openInfoPlaceActivity(FocusedPlace focusedPlace) {

        Intent intent = new Intent(this, InfoPlaceActivity.class);

        intent.putExtra(Place.ID, focusedPlace.getId());
        intent.putExtra(Place.NAME, focusedPlace.getName());
        intent.putExtra(Place.DESCRIPTION, focusedPlace.getDescription());
        intent.putExtra(Place.IMAGE, focusedPlace.getImagePath());
        intent.putExtra(Place.ICON, focusedPlace.getIconId());
        intent.putExtra(Place.LATITUDE, focusedPlace.getLatitude());
        intent.putExtra(Place.LONGITUDE, focusedPlace.getLongitude());

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        removeImages();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        removeImages();
        super.onBackPressed();
    }

    private void removeImages() {
        for (int i = 0; i < adapter.getCount(); i++) {
            try {
                adapter.getPlace(i).removeImage();
            } catch (NullPointerException ignore) {}
        }
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
        setContentView(R.layout.activity_near);

        setupToolbar();

        if (MapHandler.location == null) {
            Toast.makeText(this, R.string.can_not_get_position, Toast.LENGTH_LONG).show();
            finish();
        }

        final ArrayList<String> ids = new ArrayList<>();
        int step = 0;

        RetrofitRequest request = new RetrofitRequest(Place.PLACE, Place.NEAR);
        request.putParam(Place.LATITUDE, String.valueOf(MapHandler.location.getLatitude()));
        request.putParam(Place.LONGITUDE, String.valueOf(MapHandler.location.getLongitude()));
        request.putParam(Place.STEP, String.valueOf(step));
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                Gson gson = new Gson();
                for (int i = 0; i < response.keySet().size() - 1; i++) {
                    HashMap<String, String> temp = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);
                    ids.add(temp.get(Place.ID));
                }

                GridView gridview = findViewById(R.id.gridview);
                adapter = new NearAdapter(ids, NearActivity.this);
                gridview.setAdapter(adapter);
                gridview.setOnItemClickListener(gridviewOnItemClickListener);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
        request.makeRequest();
    }
}
