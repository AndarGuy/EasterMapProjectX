package com.example.mikhail.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MainListeners mainListeners = new MainListeners();
    PermissionHelper permissionHelper = new PermissionHelper();

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private LinearLayout nameEditLayout, profileImageContainer, findsLayout, shopLayout, accountManageLayout, settingsLayout, bugReportLayout;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionHelper.getLocationPermission(this, this);

        if (isServicesOK()) mapLoad();

        elementsLoad();
        elementsSetListeners();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapLoad();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void elementsLoad() {
        Log.d(TAG, "elementsLoad: calls");
        name = findViewById(R.id.name);
        nameEditLayout = findViewById(R.id.name_edit_layout);

        profileImageContainer = findViewById(R.id.profile_image_layout);

        findsLayout = findViewById(R.id.finds_layout);
        shopLayout = findViewById(R.id.shop_layout);
        accountManageLayout = findViewById(R.id.account_manage_layout);
        settingsLayout = findViewById(R.id.settings_layout);
        bugReportLayout = findViewById(R.id.bug_report_layout);
    }

    private void elementsSetListeners() {
        Log.d(TAG, "elementsSetListeners: calls");
        nameEditLayout.setOnClickListener(mainListeners.onClickName);

        profileImageContainer.setOnClickListener(mainListeners.onClickProfileImage);

        findsLayout.setOnClickListener(mainListeners.onClickItemDrawerMenu);
        shopLayout.setOnClickListener(mainListeners.onClickItemDrawerMenu);
        accountManageLayout.setOnClickListener(mainListeners.onClickItemDrawerMenu);
        settingsLayout.setOnClickListener(mainListeners.onClickItemDrawerMenu);
        bugReportLayout.setOnClickListener(mainListeners.onClickItemDrawerMenu);
    }


}
