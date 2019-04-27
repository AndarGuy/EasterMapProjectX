package com.example.mikhail.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.util.Route;
import com.example.mikhail.help.util.RoutesAdapter;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class RoutesActivity extends AppCompatActivity {

    private static final String ROUTE_NOW = "route_now";

    private ListView routesList;
    private ProgressBar progressBar;
    private ImageView errorImage;
    private LinearLayout currentGoal;
    private CircleImageView goalImage;
    private TextView title, body, noGoalInfo;
    private ProgressBar goalBar;
    private FrameLayout noGoal;
    private ImageButton showGoal;


    public void setCurrentGoal(int routeId) {

        goalBar.setVisibility(View.VISIBLE);
        noGoalInfo.setVisibility(View.INVISIBLE);

        SQLiteDatabase routesDB = openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor cursor = routesDB.query("routes", new String[]{"places", "stage"}, "id = ?", new String[]{String.valueOf(routeId)}, null, null, null);
        cursor.moveToFirst();
        final String placeId = cursor.getString(cursor.getColumnIndex("places")).split(";")[cursor.getInt(cursor.getColumnIndex("stage")) - 1];
        cursor.close();

        RetrofitRequest infoRequest = new RetrofitRequest(Place.PLACE, Place.GET_INFO);
        String[] params = {Place.NAME, Place.LATITUDE, Place.LONGITUDE};
        infoRequest.putParam(Place.ID, placeId);
        infoRequest.putParam(Place.PARAMS, TextUtils.join("|", params));
        infoRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                title.setText(response.get(Place.NAME));
                final Double latitude = Double.valueOf(response.get(Place.LATITUDE)), longitude = Double.valueOf(response.get(Place.LONGITUDE));
                showGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapHandler.moveCameraToPosition(new LatLng(latitude, longitude), 15f);
                        MainActivity.mDrawerLayout.closeDrawers();
                        finish();
                    }
                });
                noGoal.setVisibility(View.INVISIBLE);
                currentGoal.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        infoRequest.makeRequest();

        RetrofitRequest imageRequest = new RetrofitRequest(Place.PLACE, Place.GET_IMAGE);

        imageRequest.putParam(Place.IMAGE_SIZE, Place.S_SIZE);
        imageRequest.putParam(Place.ID, placeId);

        imageRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                String imageFileName = "LOCATION_IMAGE_" + placeId + "_" + Place.S_SIZE;
                File storageDir = getObbDir();
                String path = storageDir.getAbsolutePath() + "/" + imageFileName;
                Bitmap image = Utilities.decodeBase64(response.get(Place.IMAGE));
                Utilities.saveBitmap(image, path);
                goalImage.setImageBitmap(Utilities.getPlaceImage(placeId, Place.S_SIZE, RoutesActivity.this));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        Bitmap image = Utilities.getPlaceImage(placeId, Place.S_SIZE, RoutesActivity.this);
        if (image == null) {
            goalImage.setColorFilter(Utilities.getColorOfString(Utilities.formatName(placeId)));
            imageRequest.makeRequest();
        } else goalImage.setImageBitmap(image);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routesList = findViewById(R.id.routesList);
        progressBar = findViewById(R.id.progressBar);
        errorImage = findViewById(R.id.errorImage);
        noGoal = findViewById(R.id.noGoal);
        currentGoal = findViewById(R.id.currentGoal);
        goalImage = findViewById(R.id.goalImage);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        noGoalInfo = findViewById(R.id.noGoalInfo);
        goalBar = findViewById(R.id.goalBar);
        showGoal = findViewById(R.id.showGoal);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(ROUTE_NOW)) {

            int nowRouteId = preferences.getInt(ROUTE_NOW, -1);

            setCurrentGoal(nowRouteId);
        }

        RetrofitRequest request = new RetrofitRequest(Route.ROUTES, Route.GET);

        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                progressBar.setVisibility(View.INVISIBLE);

                ArrayList<Route> routes = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < response.keySet().size() - 1; i++) {
                    HashMap<String, String> temp = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);
                    routes.add(new Route(Integer.valueOf(temp.get(Route.ID)), temp.get(Route.NAME), temp.get(Route.DESCRIPTION), temp.get(Route.PLACES).split(";")));
                }

                routesList.setAdapter(new RoutesAdapter(RoutesActivity.this, routes));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                errorImage.setVisibility(View.VISIBLE);
            }
        });

        request.makeRequest();

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.routes);
        setSupportActionBar(myToolbar);
        setTheme(R.style.AppTheme_DarkLight);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
