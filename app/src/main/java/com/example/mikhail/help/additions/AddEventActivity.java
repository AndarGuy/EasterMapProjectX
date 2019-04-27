package com.example.mikhail.help.additions;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import retrofit2.Call;

public class AddEventActivity extends AppCompatActivity implements PositionFragment.OnPositionFragmentDataListener, SizeFragment.OnSizeFragmentDataListener, DataEventFragment.OnDataEventFragmentDataListener, DataFragment.OnDataFragmentDataListener {

    private static final String TAG = "AddEventActivity";

    private static final byte
            MAX_NAME_LENGTH = 25,
            MIN_NAME_LENGTH = 4;
    private static final int
            MAX_RADIUS = 300,
            MIN_RADIUS = 10,
            OK = 0;
    private final String
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            IMAGE = "image",
            START_DATE = "start_date",
            END_DATE = "end_date",
            DESCRIPTION = "description",
            NAME = "new_name",
            SIZE = "size",
            EVENT = "event",
            ADD = "add",
            EMAIL = "email",
            PASSWORD = "password";
    private Button buttonBack, buttonNext;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private TextView hint;
    private boolean showHints;

    private LatLng location;
    private String name, description, code;
    private Bitmap image;
    private int size;

    private Integer startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute;

    private String[] hints;

    @Override
    public void OnDateStart(int year, int month, int day) {
        this.startYear = year;
        this.startMonth = month;
        this.startDay = day;
    }

    @Override
    public void OnDateEnd(int year, int month, int day) {
        this.endYear = year;
        this.endMonth = month;
        this.endDay = day;
    }

    @Override
    public void OnTimeStart(int hours, int minutes) {
        this.startHour = hours;
        this.startMinute = minutes;
    }

    @Override
    public void OnTimeEnd(int hours, int minutes) {
        this.endHour = hours;
        this.endMinute = minutes;
    }

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
        this.location = position;
    }

    @Override
    public void OnSendSize(int size) {
        this.size = size;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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
                    if (image == null) shakeView(findViewById(R.id.imageBackground));
                    else if (name == null) shakeView(findViewById(R.id.nameTextLayout));
                    else if (description == null || description.length() <= 0)
                        shakeView(findViewById(R.id.descriptionInput));
                    else if (!DataEventFragment.isDatesOK)
                        shakeView(findViewById(R.id.chooseTimeLayout));
                    else {
                        final ProgressDialog dialog = ProgressDialog.show(AddEventActivity.this, "", getString(R.string.loading),true);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddEventActivity.this);
                        RetrofitRequest request = new RetrofitRequest(EVENT, ADD, preferences.getString(EMAIL, null), preferences.getString(PASSWORD, null));
                        request.putParam(LATITUDE, String.valueOf(location.latitude));
                        request.putParam(LONGITUDE, String.valueOf(location.longitude));
                        request.putParam(SIZE, String.valueOf(size));
                        request.putParam(IMAGE, Utilities.getStringImage(image));
                        request.putParam(NAME, name);
                        request.putParam(DESCRIPTION, description);
                        Calendar startDate = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
                        Calendar endDate = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                        request.putParam(START_DATE, dateFormat.format(startDate.getTime()));
                        request.putParam(END_DATE, dateFormat.format(endDate.getTime()));
                        request.setListener(new RequestListener() {
                            @Override
                            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                                if (result == OK) {
                                    Log.d(TAG, "onResponse: event added");
                                    dialog.setMessage(getString(R.string.event_added));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }}, 1000);
                                } else {
                                    Log.d(TAG, "onResponse: error: " + result);
                                    dialog.setMessage(getString(R.string.unknown_error) + " #" + result);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.cancel();
                                        }}, 1000);
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                Log.d(TAG, "onFailure: adding event error: " + t.toString());
                                dialog.setMessage(getString(R.string.unknown_error) + " " + t.toString());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                    }}, 1000);
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

    private void shakeView(final View v) {
        v.animate().cancel();
        v.animate().scaleY(0.9f).scaleX(0.9f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.animate().scaleX(1).scaleY(1).setDuration(100);
            }
        }).start();
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.add_event);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundlePositionFragment = new Bundle();
        PositionFragment positionFragment = new PositionFragment();
        sendPositionBundles(bundlePositionFragment, positionFragment);
        positionFragment.setArguments(bundlePositionFragment);
        adapter.addFragment(positionFragment, getResources().getString(R.string.position));

        Bundle bundleSizeFragment = new Bundle();
        final SizeFragment sizeFragment = new SizeFragment();
        sendSizeBundles(bundleSizeFragment, sizeFragment);
        sizeFragment.setArguments(bundleSizeFragment);
        adapter.addFragment(sizeFragment, getResources().getString(R.string.size));

        Bundle bundleDataFragment = new Bundle();
        DataEventFragment dataFragment = new DataEventFragment();
        sendDataBundles(bundleDataFragment, dataFragment);
        dataFragment.setArguments(bundleDataFragment);
        adapter.addFragment(dataFragment, getResources().getString(R.string.data));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setEnableSwipe(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter.getItem(position) instanceof SizeFragment)
                    sizeFragment.setLocation(location);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void sendPositionBundles(Bundle bundle, PositionFragment fragment) {
        bundle.putDouble(fragment.KEY_LATITUDE, Double.valueOf(getIntent().getExtras().getString("Latitude")));
        bundle.putDouble(fragment.KEY_LONGITUDE, Double.valueOf(getIntent().getExtras().getString("Longitude")));
        bundle.putDouble(fragment.KEY_RADIUS, 0.001);
    }

    private void sendSizeBundles(Bundle bundle, SizeFragment fragment) {
        bundle.putInt(fragment.KEY_MIN_RADIUS, MIN_RADIUS);
        bundle.putInt(fragment.KEY_MAX_RADIUS, MAX_RADIUS);
    }

    private void sendDataBundles(Bundle bundle, DataFragment fragment) {
        bundle.putByte(fragment.KEY_NAME_MAX_LEN, MAX_NAME_LENGTH);
        bundle.putByte(fragment.KEY_NAME_MIN_LEN, MIN_NAME_LENGTH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }
}
