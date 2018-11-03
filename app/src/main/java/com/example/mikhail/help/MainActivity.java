package com.example.mikhail.help;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.mikhail.help.util.PlaceAutocompleteAdapter;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.HashMap;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    MainListener listener;
    MapHandler mapHandler = new MapHandler(this);

    private final String TAG = "MainActivity";
    private final int ERROR_DIALOG_REQUEST = 9001;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String ACTION_TEST = "test";
    private final String
            PASSWORD = "password",
            EMAIL = "email",
            LOGIN = "login";
    private final int OK = 0;
    public static final String APP_PREFERENCES = "config";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mBarDrawerToggle;
    private LinearLayout mNameEditLayout, mProfileImageContainer, mFindsLayout, mShopLayout, mAccountManageLayout, mSettingsLayout, mBugReportLayout;
    private FrameLayout mFabBackground, mFabPlaceSide, mFabEventSide, mFabTextSide;
    private FloatingActionButton mFab, mFabPlace, mFabText, mFabEvent;

    private PlaceAutocompleteAdapter mAdapter;
    protected GeoDataClient mGeoDataClient;

    public boolean mLocationPermissionGranted = false;
    public boolean userAuthorized = false;


    //-------------MAIN-------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            getLocationPermission();
            mapLoad();
        }

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

        elementsSetListeners();

        userAuthorize();

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
    }

    private void elementsSetListeners() {
        Log.d(TAG, "elementsSetListeners: calls");
        mNameEditLayout.setOnClickListener(listener.onClickName);
        mDrawerLayout.addDrawerListener(mBarDrawerToggle);
        mProfileImageContainer.setOnClickListener(listener.onClickProfileImage);
        mFindsLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mShopLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mAccountManageLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mSettingsLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mBugReportLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        mFab.setOnClickListener(listener.onFabClick());
        mFabPlace.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
        mFabEvent.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
        mFabText.setOnClickListener(listener.onMiniFabClick(this, this, mapHandler));
    }

    //-------------TOOLBAR-------------


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
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, null, null);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        LinearLayout linearLayout = (LinearLayout) searchItem.getActionView();
        final ImageView imageView = new ImageView(this);
        final AutoCompleteTextView textView = new AutoCompleteTextView(this);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                mapHandler.goToPlace(textView.getText().toString());

                searchClose(textView, imageView);
            }
        });
        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    mapHandler.goToPlace(textView.getText().toString());

                    searchClose(textView, imageView);
                }
                return false;
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(imageView);

        return super.onCreateOptionsMenu(menu);
    }

    private void searchClose(AutoCompleteTextView textView, ImageView imageView) {
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

    private void toolbarLoad() {

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mBarDrawerToggle.syncState();
        mBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //-----------PERMISSIONS-----------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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

    //------------MAP UTILS------------

    private void mapLoad() {
        Log.d(TAG, "mapLoad: map loading");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapHandler);

        Log.d(TAG, "mapLoad: map loaded");

    }

    public boolean isServicesOK() {

        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occurred but we can resolve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //-------------ACCOUNT-------------

    public void userAuthorize() {
        RetrofitRequest request = new RetrofitRequest(ACTION_TEST);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, Double> response, Integer result) {
                userLogin();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.server_not_online), Toast.LENGTH_SHORT).show();
            }
        });
        request.makeRequest();
    }

    public void openAuthorizationActivity(Context context) {
        Intent intent = new Intent(context, AuthorizationActivity.class);
        startActivityForResult(intent, RESULT_OK);
    }

    public void openInternetConnection(Context context) {
        Intent intent = new Intent(context, InternetConnection.class);
        startActivityForResult(intent, RESULT_OK);
    }

    public void userLogin() {
        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(PASSWORD) && preferences.contains(EMAIL)) {
            RetrofitRequest request = new RetrofitRequest(LOGIN);
            request.putParam(EMAIL, preferences.getString(EMAIL, null));
            request.putParam(PASSWORD, preferences.getString(PASSWORD, null));
            request.setListener(new RequestListener() {
                @Override
                public void onResponse(Call<Object> call, HashMap<String, Double> response, Integer result) {
                    Log.d(TAG, "onResponse: " + result);
                    if (result != OK) {
                        openAuthorizationActivity(MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(MainActivity.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            });
            request.makeRequest();

        } else {
            openAuthorizationActivity(this);
        }
    }

    //APPEARANCE

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
