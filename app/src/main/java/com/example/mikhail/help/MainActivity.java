package com.example.mikhail.help;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    private LinearLayout nameEditLayout, profileImageContainer, findsLayout, shopLayout, accountManageLayout, settingsLayout, bugReportLayout;
    private TextView name;

    public boolean mLocationPermissionGranted = false;


    //--- Listeners ---//


    View.OnClickListener onClickName = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Do something for onClickName
        }
    };

    View.OnClickListener onClickProfileImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Do something for onClickProfileImage
        }
    };

    View.OnClickListener onClickItemDrawerMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int id = view.getId();

            switch (id) {
                case R.id.finds_layout:
                    break;
                case R.id.shop_layout:
                    break;
                case R.id.account_manage_layout:
                    break;
                case R.id.settings_layout:
                    break;
                case R.id.bug_report_layout:
                    break;
            }
        }
    };

    //--- Creates ---//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            getLocationPermission();
            mapLoad();
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        toolbarLoad();

        drawerLayout.addDrawerListener(barDrawerToggle);

        name = findViewById(R.id.name);

        nameEditLayout = findViewById(R.id.name_edit_layout);
        nameEditLayout.setOnClickListener(onClickName);

        profileImageContainer = findViewById(R.id.profile_image_layout);
        profileImageContainer.setOnClickListener(onClickProfileImage);

        findsLayout = findViewById(R.id.finds_layout);
        shopLayout = findViewById(R.id.shop_layout);
        accountManageLayout = findViewById(R.id.account_manage_layout);
        settingsLayout = findViewById(R.id.settings_layout);
        bugReportLayout = findViewById(R.id.bug_report_layout);

        findsLayout.setOnClickListener(onClickItemDrawerMenu);
        shopLayout.setOnClickListener(onClickItemDrawerMenu);
        accountManageLayout.setOnClickListener(onClickItemDrawerMenu);
        settingsLayout.setOnClickListener(onClickItemDrawerMenu);
        bugReportLayout.setOnClickListener(onClickItemDrawerMenu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (barDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    //--- Methods --//

    private void toolbarLoad() {

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.syncState();
        barDrawerToggle.setDrawerSlideAnimationEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void mapLoad() {
        Log.d(TAG, "mapLoad: map loading");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapHandler(this));

        Log.d(TAG, "mapLoad: map loaded");

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

}
