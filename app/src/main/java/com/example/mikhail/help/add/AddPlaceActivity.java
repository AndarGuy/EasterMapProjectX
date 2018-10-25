package com.example.mikhail.help.add;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mikhail.help.R;
import com.example.mikhail.help.utils.CustomViewPager;
import com.example.mikhail.help.utils.ViewPagerAdapter;
import com.google.android.gms.maps.model.LatLng;

public class AddPlaceActivity extends AppCompatActivity implements PositionFragment.OnPositionFragmentDataListener, TypeFragment.OnTypeFragmentDataListener, DataFragment.OnDataFragmentDataListener {

    private static final String TAG = "AddPlaceActivity";

    private Button buttonBack, buttonNext;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    private LatLng position;
    private String name, description, code;
    private Bitmap image;

    private byte maxNameLength = 25, minNameLength = 4;

    private int[] mThumbIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};

    private String[] mThumbCodes = {"GR", "MN", "PS", "MO", "CH", "EB", "EE"};


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
        this.code = code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        elementsLoad();

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        elementsSetListeners();

        setTabsDisable();

        setupToolbar();

    }

    private void elementsLoad() {
        tabLayout = findViewById(R.id.tabHost);

        viewPager = findViewById(R.id.viewPager);

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
        bundle.putDouble(fragment.KEY_LATITUDE, Double.valueOf(getIntent().getExtras().getString("Latitude")));
        bundle.putDouble(fragment.KEY_LONGITUDE, Double.valueOf(getIntent().getExtras().getString("Longitude")));
        bundle.putDouble(fragment.KEY_RADIUS, 0.001);
    }

    private void sendTypeBundles(Bundle bundle, TypeFragment fragment) {
        bundle.putStringArray(fragment.KEY_THUMB_CODES, mThumbCodes);
        bundle.putIntArray(fragment.KEY_THUMB_IDS, mThumbIds);

    }

    private void sendDataBundles(Bundle bundle, DataFragment fragment) {
        bundle.putByte(fragment.KEY_NAME_MAX_LEN, maxNameLength);
        bundle.putByte(fragment.KEY_NAME_MIN_LEN, minNameLength);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }
}
