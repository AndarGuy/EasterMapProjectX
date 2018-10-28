package com.example.mikhail.help.things;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.R;

public class DataAdapter extends BaseAdapter {

    private Context mContext;
    private int[] mThumbIds;
    private String[] mNames;

    public DataAdapter(Context c, int[] ids, String[] names) {
        mContext = c;
        mThumbIds = ids;
        mNames = names;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;

        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            grid = inflater.inflate(R.layout.type_list_item, parent, false);
        } else {
            grid = convertView;
        }
        ImageView imageView = grid.findViewById(R.id.image);
        TextView textView = grid.findViewById(R.id.text);
        imageView.setImageResource(mThumbIds[position]);
        textView.setText(mNames[position]);

        return grid;
    }
}