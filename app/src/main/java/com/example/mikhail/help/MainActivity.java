package com.example.mikhail.help;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    MainListener listener = new MainListener();

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    private LinearLayout nameEditLayout, profileImageContainer, findsLayout, shopLayout, accountManageLayout, settingsLayout, bugReportLayout;
    private FrameLayout fabBackGround;
    private FloatingActionButton fab, fabPlace, fabText, fabEvent;

    public boolean mLocationPermissionGranted = false;


    //-------------MAIN-------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            getLocationPermission();
            mapLoad();
        }

        elementsLoad();

        toolbarLoad();

        elementsSetListeners();

        userAuthorize();

    }

    private void elementsLoad() {
        Log.d(TAG, "elementsLoad: calls");
        nameEditLayout = findViewById(R.id.nameEditLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        profileImageContainer = findViewById(R.id.profileImageLayout);
        findsLayout = findViewById(R.id.findsLayout);
        shopLayout = findViewById(R.id.shopLayout);
        accountManageLayout = findViewById(R.id.accountManageLayout);
        settingsLayout = findViewById(R.id.settingsLayout);
        bugReportLayout = findViewById(R.id.bugReportLayout);
        fab = findViewById(R.id.floatingActionButton);
        fabEvent = findViewById(R.id.fabEvent);
        fabPlace = findViewById(R.id.fabPlace);
        fabText = findViewById(R.id.fabText);
        fabBackGround = findViewById(R.id.fabBackGround);
    }

    private void elementsSetListeners() {
        Log.d(TAG, "elementsSetListeners: calls");
        nameEditLayout.setOnClickListener(listener.onClickName);
        drawerLayout.addDrawerListener(barDrawerToggle);
        profileImageContainer.setOnClickListener(listener.onClickProfileImage);
        findsLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        shopLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        accountManageLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        settingsLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        bugReportLayout.setOnClickListener(listener.onClickItemDrawerMenu);
        fab.setOnClickListener(listener.onFabClick(fab, fabPlace, fabEvent, fabText, fabBackGround));
        fabPlace.setOnClickListener(listener.onMiniFabClick());
        fabEvent.setOnClickListener(listener.onMiniFabClick());
        fabText.setOnClickListener(listener.onMiniFabClick());
    }

    //-------------TOOLBAR-------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (barDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void toolbarLoad() {

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.syncState();
        barDrawerToggle.setDrawerSlideAnimationEnabled(false);
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
                    //mapReload();
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
        mapFragment.getMapAsync(new MapHandler(this));

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

    public void authorizeReload() {

    }

    public boolean userAuthorize() {
        if (isServerOK()) {
            if (isUserLogged()) {
                return true;
            } else {
                Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
                startActivity(intent);
            }
        }
        return false;
    }

    public boolean isServerOK() {
        return true;
    }

    public boolean isUserLogged() {
        return true;
    }

}
