package com.example.mikhail.help.additions;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.example.mikhail.help.util.CustomViewPager;
import com.example.mikhail.help.util.ViewPagerAdapter;
import com.google.android.gms.maps.model.LatLng;

public class AddEventActivity extends AppCompatActivity implements PositionFragment.OnPositionFragmentDataListener, SizeFragment.OnSizeFragmentDataListener, DataFragment.OnDataFragmentDataListener {

    private static final String TAG = "AddEventActivity";

    private static final byte
            MAX_NAME_LENGTH = 25,
            MIN_NAME_LENGTH = 4;
    private static int
            MAX_RADIUS = 300,
            MIN_RADIUS = 10;

    private Button buttonBack, buttonNext;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private TextView hint;

    private LatLng location;
    private String name, description, code;
    private Bitmap image;
    private int size;

    private String[] hints;


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

        hints = getResources().getStringArray(R.array.add_event_hints);

        hint.setText(hints[0]);
        hint.animate().setDuration(6000).alpha(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hint.animate().setDuration(6000).alpha(0);
            }

            @Override
            public void onPageSelected(int position) {
                hint.setText(hints[position]);
                hint.animate().cancel();
                hint.setAlpha(1);
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
                    //finish();
                    //TODO: make request
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
        DataFragment dataFragment = new DataFragment();
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
                if (adapter.getItem(position) instanceof SizeFragment) sizeFragment.setLocation(location);
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
