package com.example.mikhail.help;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mikhail.help.util.Route;
import com.example.mikhail.help.util.RoutesAdapter;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class RoutesActivity extends AppCompatActivity {

    private static final String ROUTE_NOW = "route_now";

    private ListView routesList;
    private ProgressBar progressBar;
    private ImageView errorImage;
    private LinearLayout noGoal, currentGoal;
    private CircleImageView goalImage;
    private TextView title, body;


    public void setCurrentGoal(String placeId) {

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(ROUTE_NOW)) {
            noGoal.setVisibility(View.INVISIBLE);
            currentGoal.setVisibility(View.VISIBLE);

            Integer nowRouteId = Integer.getInteger(preferences.getString(ROUTE_NOW, "0"));

            // title.setText(nowRouteId);
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

                routesList.setAdapter(new RoutesAdapter(getApplicationContext(), routes));
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
