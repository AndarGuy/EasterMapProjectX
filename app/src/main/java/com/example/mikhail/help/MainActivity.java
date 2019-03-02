package com.example.mikhail.help;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.util.FocusedPlace;
import com.example.mikhail.help.util.NameHelper;
import com.example.mikhail.help.util.MapAutocompleteAdapter;
import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.util.PlaceAutocompleteAdapter;
import com.example.mikhail.help.util.Utilities;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int REQUEST_ACCOUNT = 2;
    public boolean mLocationPermissionGranted = false;
    protected GeoDataClient mGeoDataClient;
    private final static String NAME = "name";
    MainListener listener;
    MapHandler mapHandler = new MapHandler(this);
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mBarDrawerToggle;
    private LinearLayout mNameEditLayout, mProfileImageContainer, mFindsLayout, mShopLayout, mAccountManageLayout, mSettingsLayout, mBugReportLayout;
    private FrameLayout mFabBackground, mFabPlaceSide, mFabEventSide, mFabTextSide;
    private FloatingActionButton mFab, mFabPlace, mFabText, mFabEvent;
    private TextView mNameView;
    private PlaceAutocompleteAdapter mAdapter;


    //-------------MAIN-------------

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();
        mapLoad();

        //TODO: replace to AsyncTask
        /* new Thread() {
            @Override
            public void run() {
                if (!isNetworkConnected(MainActivity.this)) {
                    openInternetConnection(MainActivity.this);
                    MainActivity.this.finish();
                }
                else {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {}
                    run();
                }
            }
        }.start(); */

        elementsLoad();

        listener = new MainListener(mFab, mFabPlace, mFabEvent, mFabText, mFabBackground, mFabPlaceSide, mFabEventSide, mFabTextSide);

        toolbarLoad();

        showName(PreferenceManager.getDefaultSharedPreferences(this).getString(NAME, getString(R.string.loading)));

        elementsSetListeners();

    }

    @Override
    public void onBackPressed() {
        if (listener.isFabMenuOpen) {
            listener.fabMenuClose();
        } else {
            super.onBackPressed();
        }
    }

    private void elementsLoad() {
        Log.d(TAG, "elementsLoad: calls");
        mNameEditLayout = findViewById(R.id.nameEditLayout);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mProfileImageContainer = findViewById(R.id.profileImageLayout);
        mFindsLayout = findViewById(R.id.findsLayout);
        mShopLayout = findViewById(R.id.shopLayout);
        mAccountManageLayout = findViewById(R.id.accountManageLayout);
        mSettingsLayout = findViewById(R.id.settingsLayout);
        mBugReportLayout = findViewById(R.id.bugReportLayout);
        mFab = findViewById(R.id.floatingActionButton);
        mFabEvent = findViewById(R.id.fabEvent);
        mFabPlace = findViewById(R.id.fabPlace);
        mFabText = findViewById(R.id.fabText);
        mFabBackground = findViewById(R.id.fabBackGround);
        mFabPlaceSide = findViewById(R.id.fabPlaceSide);
        mFabEventSide = findViewById(R.id.fabEventSide);
        mFabTextSide = findViewById(R.id.fabTextSide);
        mNameView = findViewById(R.id.name);
    }

    //-------------TOOLBAR-------------

    private void elementsSetListeners() {
        Log.d(TAG, "elementsSetListeners: calls");
        mNameEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameLoading();
                NameHelper.generateName(MainActivity.this, new NameHelper.NameListener() {
                    @Override
                    public void onReturned(String name) {
                        showName(name);
                    }

                    @Override
                    public void onFailed(Throwable t) {
                        showName(getResources().getString(R.string.error_occurred));
                    }
                });
            }
        });
        mDrawerLayout.addDrawerListener(mBarDrawerToggle);
        mProfileImageContainer.setOnClickListener(listener.onClickProfileImage);
        mFindsLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mShopLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mAccountManageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), AccountSettingsActivity.class));
            }
        });
        mSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            }
        });
        mBugReportLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mBugReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ReportActivity.class));
            }
        });
        mFab.setOnClickListener(listener.onFabClick());
        mFabPlace.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
        mFabEvent.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
        mFabText.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        int dp8 = Utilities.getPxFromDp(8, this), dp16 = Utilities.getPxFromDp(16, this);

        mGeoDataClient = Places.getGeoDataClient(this);
        mAdapter = new PlaceAutocompleteAdapter(this);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        LinearLayout linearLayout = (LinearLayout) searchItem.getActionView();
        final ImageView imageView = new ImageView(this);
        final AutoCompleteTextView textView = new AutoCompleteTextView(this);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels - (dp16 * 8), LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        imageLayoutParams.gravity = Gravity.CENTER;
        imageLayoutParams.setMarginEnd(dp16);
        imageLayoutParams.setMarginStart(dp16);

        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageDrawable(getDrawable(R.drawable.ic_search));
        imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textView.isEnabled()) {
                    searchOpen(textView, imageView);
                } else {
                    searchClose(textView, imageView);
                }
            }
        });


        textLayoutParams.setMargins(0, dp8, 0, dp8);

        textView.setScaleX(0);
        textView.setEnabled(false);
        textView.setLayoutParams(textLayoutParams);
        textView.setInputType(InputType.TYPE_CLASS_TEXT);
        textView.setAdapter(mAdapter);
        textView.setLines(1);
        textView.setTextColor(getResources().getColor(R.color.white));
        Drawable bg = getDrawable(R.drawable.mini_fab_bg);
        bg.setAlpha(20);
        textView.setBackground(bg);
        textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        textView.setLayoutParams(textLayoutParams);
        textView.setPadding(dp8, 0, dp8, 0);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // mapHandler.goToPlace(textView.getText().toString());

                Place place = (Place) adapterView.getItemAtPosition(i);
                mapHandler.moveCameraToPosition(place.getLocation(), 15f);

                searchClose(textView, imageView);
            }
        });
        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // mapHandler.goToPlace(textView.getText().toString());

                    mapHandler.moveCameraToPosition(mAdapter.getItem(0).getLocation(), 15f);

                    searchClose(textView, imageView);
                }
                return false;
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(imageView);

        return super.onCreateOptionsMenu(menu);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchClose(AutoCompleteTextView textView, ImageView imageView) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(1, 0);
        textView.clearFocus();
        textView.setScaleX(0f);
        textView.setEnabled(false);
        imageView.setImageDrawable(getDrawable(R.drawable.ic_search));
        imageView.setRotation(0);
    }

    private void searchOpen(AutoCompleteTextView textView, ImageView imageView) {
        textView.setScaleX(1f);
        textView.setEnabled(true);
        textView.requestFocus();
        textView.selectAll();
        imageView.setImageDrawable(getDrawable(R.drawable.ic_plus));
        imageView.setRotation(45);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textView, 0);
    }

    //-----------PERMISSIONS-----------

    private void toolbarLoad() {

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mBarDrawerToggle.syncState();
        mBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                }
        }
    }

    //------------MAP UTILS------------

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission.");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: already permissions granted");
            mLocationPermissionGranted = true;
        } else {
            Log.d(TAG, "getLocationPermission: requesting permissions");
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void mapLoad() {
        Log.d(TAG, "mapLoad: map loading");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapHandler);

        Log.d(TAG, "mapLoad: map loaded");

    }

    //-------------ACCOUNT-------------

    private void showName(String name) {
        mNameEditLayout.setEnabled(true);
        StringBuilder formattedName = new StringBuilder();
        try {
            for (String s : name.split("_")) {
                if (s.length() > 1) {
                    formattedName.append(String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));
                } else {
                    formattedName.append(s.toUpperCase());
                }
            }
            mNameView.setText(formattedName);
        } catch (Exception e) {
            Log.d(TAG, "onNickFormatting: formatting error! " + e.toString());
            mNameView.setText(name);
        }
    }

    private void showNameLoading() {
        mNameView.setText(getString(R.string.loading));
        mNameEditLayout.setEnabled(false);
    }

}
