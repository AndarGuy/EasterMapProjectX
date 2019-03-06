package com.example.mikhail.help.util;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.MapHandler;
import com.example.mikhail.help.R;
import com.example.mikhail.help.web.Answer;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceAutocompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 5;
    private static final String TAG = "PlaceAutocompleteAdapte";
    private final static String
            SEARCH_ACTION = "search",
            PLACE = "place",
            TEXT = "text",
            ID = "id",
            TYPE = "type",
            LATITUDE = "latitude",
            LONGITUDE = "longitude",
            NAME = "name";
    private final static int OK = 0;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private List<Place> mResults;
    private Context mContext;

    public PlaceAutocompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<Place>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Place getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
        }

        Place place = getItem(position);

        Location placeLocation = new Location(place.getId());
        placeLocation.setLatitude(place.getLatitude());
        placeLocation.setLongitude(place.getLongitude());

        Location myLocation = MapHandler.location;

        TextView textView1 = convertView.findViewById(R.id.text1);
        TextView textView2 = convertView.findViewById(R.id.text2);
        ImageView imageView1 = convertView.findViewById(R.id.image1);
        textView1.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        textView2.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        imageView1.setImageBitmap(place.getIcon());
        textView1.setText(place.getName());
        Log.d(TAG, "getView: " + myLocation + " " + placeLocation);

        if (myLocation == null) {
            textView2.setText(place.getId());
        } else {
            float distance = placeLocation.distanceTo(myLocation);
            textView2.setText(Utilities.formatDistance(distance, mContext));
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Place> places = findPlaces(mContext, constraint.toString());

                    filterResults.values = places;
                    filterResults.count = places.size();
                }

                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<Place>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }


    private List<Place> findPlaces(Context context, CharSequence constraint) {
        ArrayList<Place> places = new ArrayList<>();

        RetrofitRequest request = new RetrofitRequest(SEARCH_ACTION, PLACE);
        request.putParam(TEXT, constraint.toString());
        Answer answer = request.makeExecuteRequest();

        if (answer != null) {
            if (answer.getResult() == OK) {
                Log.d(TAG, "onResponse: " + String.valueOf(answer.getResult()) + " " + answer.getResponse());

                Gson gson = new Gson();
                for (int i = 0; i < answer.getResponse().keySet().size() - 1; i++) {

                    HashMap<String, String> tempPlace = gson.fromJson(gson.toJson(answer.getResponse().get(String.valueOf(i))), HashMap.class);
                    Place currentPlace = new Place(tempPlace.get(ID), tempPlace.get(TYPE), Double.valueOf(tempPlace.get(LATITUDE)), Double.valueOf(tempPlace.get(LONGITUDE)));
                    currentPlace.setName(tempPlace.get(NAME));
                    try {
                        currentPlace.createIcon(mContext);
                    } catch (IndexOutOfBoundsException e) {
                        currentPlace.setIcon(Utilities.drawableToBitmap(mContext.getDrawable(R.drawable.ic_sync)));
                    }

                    places.add(currentPlace);
                }
            }
        } else {
            Log.d(TAG, "onFailure: Fail search!");
        }

        return places;
    }
}