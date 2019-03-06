package com.example.mikhail.help.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mikhail.help.MapHandler;
import com.example.mikhail.help.R;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

public class NearAdapter extends BaseAdapter {

    private static final String TAG = "NearAdapter";

    private ArrayList<String> mIds;
    private SparseArray<FocusedPlace> mPlace = new SparseArray<>();
    private Context mContext;

    public NearAdapter(ArrayList<String> ids, Context context) {
        this.mIds = ids;
        this.mContext = context;
    }

    public FocusedPlace getPlace(int position) {
        return mPlace.get(position);
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
            grid = inflater.inflate(R.layout.near_item, parent, false);
        } else {
            grid = convertView;
        }

        final ImageView background = grid.findViewById(R.id.background);
        final TextView title = grid.findViewById(R.id.title), distance = grid.findViewById(R.id.distance);
        final ProgressBar bar = grid.findViewById(R.id.loadingIndicator);

        final RetrofitRequest request = new RetrofitRequest(Place.PLACE, Place.INFO);
        request.putParam(Place.ID, getItem(position).toString());
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                FocusedPlace focusedPlace = new FocusedPlace(new Place(getItem(position).toString(), Double.valueOf(response.get(Place.LATITUDE)), Double.valueOf(response.get(Place.LONGITUDE))));
                focusedPlace.setImage(Utilities.decodeBase64(response.get(Place.IMAGE)), mContext);
                focusedPlace.setName(response.get(Place.NAME));
                focusedPlace.setType(response.get(Place.TYPE));

                background.setImageBitmap(focusedPlace.getImage());
                title.setText(focusedPlace.getName());
                distance.setText(Utilities.formatDistance(Double.valueOf(SphericalUtil.computeDistanceBetween(focusedPlace.getLocation(), new LatLng(MapHandler.location.getLatitude(), MapHandler.location.getLongitude()))).floatValue(), mContext));
                bar.setVisibility(View.INVISIBLE);

                mPlace.append(position, focusedPlace);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
        request.makeRequest();

        return grid;
    }
}
