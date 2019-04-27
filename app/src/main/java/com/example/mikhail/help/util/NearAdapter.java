package com.example.mikhail.help.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mikhail.help.MapHandler;
import com.example.mikhail.help.NearActivity;
import com.example.mikhail.help.R;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonSerializer;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;

public class NearAdapter extends BaseAdapter {

    private static final String TAG = "NearAdapter";

    private ArrayList<String> mIds;
    private Context mContext;

    public NearAdapter(ArrayList<String> ids, Context context) {
        this.mIds = ids;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;

        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = (FrameLayout) inflater.inflate(R.layout.near_item, parent, false);
        } else {
            grid = convertView;
        }


        int marging = Utilities.getPxFromDp(4, mContext);
        int size = Float.valueOf((Resources.getSystem().getDisplayMetrics().widthPixels - (4 * marging)) / 3).intValue();

        grid.setLayoutParams(new FrameLayout.LayoutParams(size, size));

        final ImageView background = grid.findViewById(R.id.background);
        final TextView title = grid.findViewById(R.id.title), distance = grid.findViewById(R.id.distance);
        final ProgressBar bar = grid.findViewById(R.id.loadingIndicator);


        RetrofitRequest infoRequest = new RetrofitRequest(Place.PLACE, Place.GET_INFO);

        String[] params = {Place.NAME, Place.LATITUDE, Place.LONGITUDE};

        infoRequest.putParam(Place.ID, mIds.get(position));
        infoRequest.putParam(Place.PARAMS, TextUtils.join("|", params));
        infoRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                title.setText(response.get(Place.NAME));
                Double latitude = Double.valueOf(response.get(Place.LATITUDE)), longitude = Double.valueOf(response.get(Place.LONGITUDE));
                distance.setText(Utilities.formatDistance(Double.valueOf(SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude), new LatLng(MapHandler.location.getLatitude(), MapHandler.location.getLongitude()))).floatValue(), mContext));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        infoRequest.makeRequest();


        final RetrofitRequest imageRequest = new RetrofitRequest(Place.PLACE, Place.GET_IMAGE);

        imageRequest.putParam(Place.IMAGE_SIZE, Place.S_SIZE);
        imageRequest.putParam(Place.ID, mIds.get(position));

        imageRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                String imageFileName = "LOCATION_IMAGE_" + mIds.get(position) + "_" + Place.S_SIZE;
                File storageDir = mContext.getObbDir();
                String path = storageDir.getAbsolutePath() + "/" + imageFileName;
                Bitmap image = Utilities.decodeBase64(response.get(Place.IMAGE));
                Utilities.saveBitmap(image, path);
                background.setImageBitmap(BlurBuilder.blur(mContext, Bitmap.createScaledBitmap(image, 300, 300, false)));
                bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        Bitmap image = Utilities.getPlaceImage(mIds.get(position), Place.S_SIZE, mContext);
        if (image == null) imageRequest.makeRequest();
        else {
            background.setImageBitmap(BlurBuilder.blur(mContext, Bitmap.createScaledBitmap(image, 300, 300, false)));
            bar.setVisibility(View.INVISIBLE);
        }


        return grid;
    }
}
