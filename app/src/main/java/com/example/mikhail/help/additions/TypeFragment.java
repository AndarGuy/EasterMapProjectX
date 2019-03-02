package com.example.mikhail.help.additions;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.example.mikhail.help.util.DataAdapter;


public class TypeFragment extends Fragment {

    private static final String TAG = "TypeFragment";

    public static final String
            KEY_THUMB_IDS = "ids",
            KEY_THUMB_CODES = "codes",
            KEY_THUMB_NAMES = "names";

    private TextView mSelectText;
    private ImageView mSelectImage;

    private int[] mThumbIds;
    private String[] mThumbCodes;
    private String[] mThumbNames;

    public TypeFragment() {

    }

    private void getBundles() {
        mThumbIds = getArguments().getIntArray(KEY_THUMB_IDS);
        mThumbCodes = getArguments().getStringArray(KEY_THUMB_CODES);
        mThumbNames = getArguments().getStringArray(KEY_THUMB_NAMES);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_type, container, false);

        getBundles();

        GridView gridview = v.findViewById(R.id.gridview);
        gridview.setAdapter(new DataAdapter(this.getContext(), mThumbIds, mThumbNames));

        gridview.setOnItemClickListener(gridviewOnItemClickListener);

        return v;
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (mSelectImage != null) {
                mSelectImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
            }
            if (mSelectText != null) {
                mSelectText.setTextColor(getResources().getColor(R.color.darkGrey));
            }
            mSelectText = v.findViewById(R.id.text);
            mSelectImage = v.findViewById(R.id.image);
            mSelectText.setTextColor(getResources().getColor(R.color.colorAccent));
            mSelectImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mListener.OnSendCode(mThumbCodes[position]);
        }

    };

    public interface OnTypeFragmentDataListener {
        void OnSendCode(String code);
    }

    private OnTypeFragmentDataListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTypeFragmentDataListener) {
            mListener = (OnTypeFragmentDataListener) context;
        }
    }

}
