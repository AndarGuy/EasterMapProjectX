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

import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class InternetConnection extends AppCompatActivity {

    private static final String TAG = "InternetConnection";

    private static final String TEST = "test";

    ImageView retry, emoji;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection);

        retry = findViewById(R.id.retryButton);
        info = findViewById(R.id.info);
        emoji = findViewById(R.id.emojiView);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitRequest request = new RetrofitRequest(TEST);
                retry.setEnabled(false);
                request.setListener(new RequestListener() {
                    @Override
                    public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                        emoji.setImageResource(R.drawable.ic_shocked);
                        Utilities.setColoredText(info, getResources().getString(R.string.success), Color.LTGRAY);
                        Intent intent = new Intent(InternetConnection.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        retry.setEnabled(true);
                        Utilities.setColoredText(info, getResources().getString(R.string.no_connection), Color.LTGRAY);
                    }
                });
                request.makeRequest();
            }
        });


    }

    public static boolean isNetworkConnected(Context context) {
        Log.d(TAG, "isNetworkConnected: Checking Internet connection!");

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) networkInfo = connectivityManager.getActiveNetworkInfo();

        Log.d(TAG, "isNetworkConnected: Internet enable: " + (networkInfo != null && networkInfo.isConnected()));

        return networkInfo != null && networkInfo.isConnected();

    }

}
