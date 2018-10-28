package com.example.mikhail.help;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.things.Utilities;

public class InternetConnection extends AppCompatActivity {

    private static final String TAG = "InternetConnection";

    Utilities utils = new Utilities();

    ImageView retry;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection);

        retry = findViewById(R.id.retryButton);
        info = findViewById(R.id.info);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected(InternetConnection.this)) {
                    utils.setColoredText(info, getResources().getString(R.string.success), Color.LTGRAY);
                    Intent intent = new Intent(InternetConnection.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    utils.setColoredText(info, getResources().getString(R.string.no_connection), Color.LTGRAY);
                }
            }
        });


    }

    public static boolean isNetworkConnected(Context context) {
        Log.d(TAG, "isNetworkConnected: Checking Internet connection!");

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        Log.d(TAG, "isNetworkConnected: Internet enable: " + (networkInfo != null && networkInfo.isConnected()));

        return networkInfo != null && networkInfo.isConnected();

    }

}
