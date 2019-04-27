package com.example.mikhail.help.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikhail.help.R;
import com.example.mikhail.help.RoutesActivity;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

public class RoutesAdapter implements ListAdapter {

    private static final String TAG = "RoutesAdapter";

    private static final String ROUTE_NOW = "route_now";

    ArrayList<Route> routes;
    Context context;
    private boolean isRouteExtended = false;

    public RoutesAdapter(Context context, ArrayList<Route> routes) {
        this.context = context;
        this.routes = routes;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int position) {
        return routes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Route route = routes.get(position);

        View view = View.inflate(context, R.layout.route_item, null);

        TextView nameView = view.findViewById(R.id.name), descriptionView = view.findViewById(R.id.description);
        final CircleImageView imageView = view.findViewById(R.id.image);
        final ImageView downButton = view.findViewById(R.id.downButton);
        final LinearLayout routeContainer = view.findViewById(R.id.routeContainer), routeExtend = (LinearLayout) View.inflate(context, R.layout.route_item_extend, null);
        LinearLayout goalsImageConstructor = routeExtend.findViewById(R.id.goalsImageConstructor);


        final SQLiteDatabase routesDB = context.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        routesDB.execSQL("CREATE TABLE IF NOT EXISTS routes (id INT, places VARCHAR(200), stage INT)");

        Cursor cursor = routesDB.query("routes", new String[]{"stage"}, "id = ?", new String[]{String.valueOf(route.getId())}, null, null, null);
        int stage = 0;
        if (cursor.moveToFirst()) stage = cursor.getInt(cursor.getColumnIndex("stage"));
        cursor.close();
        int placesCount = route.getPlaces().length;

        for (int i = 0; i < placesCount; i++) {
            ImageView temp = new ImageView(context);
            if (i < stage) temp.setImageResource(R.drawable.ic_visited_place);
            else temp.setImageResource(R.drawable.ic_not_visited_place);
            goalsImageConstructor.addView(temp);
            ImageView tempSep = new ImageView(context);
            tempSep.setImageResource(R.drawable.ic_route_place_connection);
            if (i < placesCount - 1) goalsImageConstructor.addView(tempSep);
        }

        final String firstPlaceId = route.getPlaces()[0];

        RetrofitRequest request = new RetrofitRequest(Place.PLACE, Place.GET_IMAGE);

        request.putParam(Place.IMAGE_SIZE, Place.S_SIZE);
        request.putParam(Place.ID, firstPlaceId);

        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                String imageFileName = "LOCATION_IMAGE_" + firstPlaceId + "_" + Place.S_SIZE;
                File storageDir = context.getObbDir();
                String path = storageDir.getAbsolutePath() + "/" + imageFileName;
                Bitmap image = Utilities.decodeBase64(response.get(Place.IMAGE));
                Utilities.saveBitmap(image, path);
                imageView.setImageBitmap(Utilities.getPlaceImage(firstPlaceId, Place.S_SIZE, context));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        Bitmap image = Utilities.getPlaceImage(firstPlaceId, Place.S_SIZE, context);
        if (image == null) {
            imageView.setColorFilter(Utilities.getColorOfString(Utilities.formatName(route.getName())));
            request.makeRequest();
        } else imageView.setImageBitmap(image);

        nameView.setText(route.getName());
        descriptionView.setText(route.getDescription());


        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRouteExtended) {
                    routeContainer.removeView(routeExtend);
                    downButton.animate().rotation(0);
                    isRouteExtended = !isRouteExtended;
                } else {
                    routeContainer.addView(routeExtend);
                    downButton.animate().rotation(180);
                    isRouteExtended = !isRouteExtended;
                }
            }
        });

        routeExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (preferences.contains(ROUTE_NOW) && preferences.getInt(ROUTE_NOW, -1) == route.getId()) {
                    Toast.makeText(context, R.string.you_already_choosen_this, Toast.LENGTH_LONG).show();
                    return;
                }

                preferences.edit().putInt(ROUTE_NOW, route.getId()).apply();

                Cursor cursor = routesDB.query("routes", new String[]{"places", "stage"}, "id = ?", new String[]{String.valueOf(route.getId())}, null, null, null);

                int stage = 1;

                if (cursor.moveToFirst()) {
                    stage = cursor.getInt(cursor.getColumnIndex("stage"));
                } else {
                    ContentValues row = new ContentValues();
                    row.put("id", route.getId());
                    row.put("places", TextUtils.join(";", route.getPlaces()));
                    row.put("stage", stage);

                    routesDB.insert("routes", null, row);
                }
                cursor.close();

                ((RoutesActivity) context).setCurrentGoal(route.getId());
            }
        });

        return view;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
