package com.example.mikhail.help.additions;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikhail.help.R;
import com.example.mikhail.help.util.CustomViewPager;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.util.ViewPagerAdapter;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import retrofit2.Call;

public class AddPlaceActivity extends AppCompatActivity implements PositionFragment.OnPositionFragmentDataListener, TypeFragment.OnTypeFragmentDataListener, DataFragment.OnDataFragmentDataListener {

    public static final String APP_PREFERENCES = "config";
    private static final String TAG = "AddPlaceActivity";
    private final String
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            IMAGE = "image",
            TYPE = "type",
            DESCRIPTION = "description",
            NAME = "new_name",
            PLACE = "mPlace",
            ADD = "add",
            EMAIL = "email",
            PASSWORD = "password";
    private final int OK = 0;
    private final byte maxNameLength = 25, minNameLength = 4;
    private final int[] mThumbIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};
    private final String[] mThumbTypes = {"GR", "MN", "PS", "MO", "CH", "EB", "EG"};
    private Button buttonBack, buttonNext;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private TextView hint;
    private LatLng position;
    private String name, description, type;
    private Bitmap image;
    private boolean showHints;

    @Override
    public void OnSendName(String name) {
        this.name = name;
    }

    @Override
    public void OnSendDescription(String description) {
        this.description = description;
    }

    @Override
    public void OnSendImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public void OnSendPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public void OnSendCode(String code) {
        this.type = code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        elementsLoad();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        showHints = pref.getBoolean("showHint", true);
        if (showHints) hint.setVisibility(View.VISIBLE);
        else hint.setVisibility(View.INVISIBLE);

        setHintText(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setHintText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        elementsSetListeners();

        setTabsDisable();

        setupToolbar();

    }

    private void setHintText(int position) {
        if (showHints) {
            hint.setText(getResources().getStringArray(R.array.add_place_hints)[position]);
        }
    }

    private void elementsLoad() {
        tabLayout = findViewById(R.id.tabHost);

        viewPager = findViewById(R.id.viewPager);

        hint = findViewById(R.id.hint);

        buttonBack = findViewById(R.id.buttonBack);
        buttonNext = findViewById(R.id.buttonNext);
    }

    private void shakeView(final View v) {
        v.animate().cancel();
        v.animate().scaleY(0.9f).scaleX(0.9f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.animate().scaleX(1).scaleY(1).setDuration(100);
            }
        }).start();
    }


    private void elementsSetListeners() {

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabLayout.getSelectedTabPosition() > 0) {
                    tabLayout.getTabAt(tabLayout.getSelectedTabPosition() - 1).select();
                } else {
                    finish();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabLayout.getSelectedTabPosition() < tabLayout.getTabCount() - 1) {
                    tabLayout.getTabAt(tabLayout.getSelectedTabPosition() + 1).select();
                } else {
                    if (type == null)
                        tabLayout.getTabAt(tabLayout.getSelectedTabPosition() - 1).select();
                    else if (image == null) shakeView(findViewById(R.id.imageBackground));
                    else if (name == null) shakeView(findViewById(R.id.nameTextLayout));
                    else if (description == null || description.length() <= 0) shakeView(findViewById(R.id.descriptionInput));
                    else {
                        Log.d(TAG, "onClick: " + name + " " + name.length());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddPlaceActivity.this);
                        final RetrofitRequest request = new RetrofitRequest(PLACE, ADD, preferences.getString(EMAIL, null), preferences.getString(PASSWORD, null));
                        request.putParam(NAME, name);
                        request.putParam(DESCRIPTION, description);
                        request.putParam(TYPE, type);
                        request.putParam(IMAGE, Utilities.getStringImage(image));
                        request.putParam(LATITUDE, String.valueOf(position.latitude));
                        request.putParam(LONGITUDE, String.valueOf(position.longitude));
                        request.setListener(new RequestListener() {
                            @Override
                            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                                if (result == OK) {
                                    Log.d(TAG, "onResponse: mPlace added");
                                    Toast.makeText(AddPlaceActivity.this, getString(R.string.place_added), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "onResponse: error: " + result);
                                    Toast.makeText(AddPlaceActivity.this, getString(R.string.place_adding_error), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                Log.d(TAG, "onFailure: adding mPlace error: " + t.toString());
                                Toast.makeText(AddPlaceActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        request.makeRequest();
                        finish();
                    }
                }
            }
        });
    }

    private void setTabsDisable() {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.add_place);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundlePositionFragment = new Bundle();
        PositionFragment positionFragment = new PositionFragment();
        sendPositionBundles(bundlePositionFragment, positionFragment);
        positionFragment.setArguments(bundlePositionFragment);
        adapter.addFragment(positionFragment, getResources().getString(R.string.position));

        Bundle bundleTypeFragment = new Bundle();
        TypeFragment typeFragment = new TypeFragment();
        sendTypeBundles(bundleTypeFragment, typeFragment);
        typeFragment.setArguments(bundleTypeFragment);
        adapter.addFragment(typeFragment, getResources().getString(R.string.type));

        Bundle bundleDataFragment = new Bundle();
        DataFragment dataFragment = new DataFragment();
        sendDataBundles(bundleDataFragment, dataFragment);
        dataFragment.setArguments(bundleDataFragment);
        adapter.addFragment(dataFragment, getResources().getString(R.string.data));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setEnableSwipe(false);
    }

    private void sendPositionBundles(Bundle bundle, PositionFragment fragment) {
        bundle.putDouble(PositionFragment.KEY_LATITUDE, Double.valueOf(getIntent().getExtras().getString("Latitude")));
        bundle.putDouble(PositionFragment.KEY_LONGITUDE, Double.valueOf(getIntent().getExtras().getString("Longitude")));
        bundle.putDouble(PositionFragment.KEY_RADIUS, 0.001);
    }

    private void sendTypeBundles(Bundle bundle, TypeFragment fragment) {
        bundle.putStringArray(TypeFragment.KEY_THUMB_CODES, mThumbTypes);
        bundle.putIntArray(TypeFragment.KEY_THUMB_IDS, mThumbIds);
        bundle.putStringArray(TypeFragment.KEY_THUMB_NAMES, getResources().getStringArray(R.array.types_places));

    }

    private void sendDataBundles(Bundle bundle, DataFragment fragment) {
        bundle.putByte(DataFragment.KEY_NAME_MAX_LEN, maxNameLength);
        bundle.putByte(DataFragment.KEY_NAME_MIN_LEN, minNameLength);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }
}
