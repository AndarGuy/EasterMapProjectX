package com.example.mikhail.help.additions;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.example.mikhail.help.util.CustomViewPager;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.util.ViewPagerAdapter;
import com.google.android.gms.maps.model.LatLng;

public class AddPlaceActivity extends AppCompatActivity implements PositionFragment.OnPositionFragmentDataListener, TypeFragment.OnTypeFragmentDataListener, DataFragment.OnDataFragmentDataListener {

    private static final String TAG = "AddPlaceActivity";

    private Button buttonBack, buttonNext;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private TextView hint;

    private LatLng position;
    private String name, description, type;
    private Bitmap image;

    private final byte maxNameLength = 25, minNameLength = 4;

    private final int[] mThumbIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};

    private final String[] mThumbTypes = {"GR", "MN", "PS", "MO", "CH", "EB", "EE"};


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

        hint.setText(getResources().getStringArray(R.array.add_place_hints)[0]);
        hint.animate().setDuration(6000).alpha(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hint.animate().setDuration(6000).alpha(0);
            }

            @Override
            public void onPageSelected(int position) {
                hint.setText(getResources().getStringArray(R.array.add_place_hints)[position]);
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
                    Log.d(TAG, "onClick: " + Utilities.getStringImage(image) + " " + image.getByteCount());
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
        bundle.putStringArray(fragment.KEY_THUMB_CODES, mThumbTypes);
        bundle.putIntArray(fragment.KEY_THUMB_IDS, mThumbIds);
        bundle.putStringArray(fragment.KEY_THUMB_NAMES, getResources().getStringArray(R.array.types_places));

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
