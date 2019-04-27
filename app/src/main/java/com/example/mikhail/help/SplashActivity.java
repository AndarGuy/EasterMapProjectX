package com.example.mikhail.help;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mikhail.help.tutorial.TutorialActivity;
import com.example.mikhail.help.util.NameHelper;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.util.SharedPreferencesUtils;

import java.io.File;
import java.util.HashMap;

import retrofit2.Call;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final String PASSWORD = "password",
    NAME = "name",
    EMAIL = "email",
    LOGIN = "login";
    private final int ERROR_DIALOG_REQUEST = 9001, OK = 0;
    private final String ACTION_TEST = "test";
    private static final String IS_FIRST_RUNNING = "ISFIRSTRUNNING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferences.edit().remove("route_now").apply();

        if (preferences.getBoolean(IS_FIRST_RUNNING, true)) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
            return;
        }

        if (!isServicesOK()) startErrorConnectionActivity();
        checkConnection();

    }

    private void startAuthorizationActivity() {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
        finish();
    }

    private void startErrorConnectionActivity() {
        Intent intent = new Intent(this, InternetConnection.class);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkConnection() {
        RetrofitRequest request = new RetrofitRequest(ACTION_TEST);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                userAuthorize();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                startErrorConnectionActivity();
            }
        });
        request.makeRequest();
    }

    public boolean isServicesOK() {

        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void userAuthorize() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(PASSWORD) && preferences.contains(EMAIL)) {
            String email = preferences.getString(EMAIL, null), password = preferences.getString(PASSWORD, null);
            RetrofitRequest request = new RetrofitRequest(LOGIN, email, password);
            request.setListener(new RequestListener() {
                @Override
                public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                    Log.d(TAG, "onResponse: userAuthorize " + result);
                    if (result != OK) {
                        startAuthorizationActivity();
                    } else {
                        NameHelper.getName(SplashActivity.this, new NameHelper.NameListener() {
                            @Override
                            public void onReturned(String name) {
                                preferences.edit().putString(NAME, name).apply();
                                startMainActivity();
                            }

                            @Override
                            public void onFailed(Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(SplashActivity.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            });
            request.makeRequest();

        } else {
            startAuthorizationActivity();
        }
    }
}