package com.example.mikhail.help.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class NameHelper {

    private static final String TAG = "NameHelper";

    private static final String
            NEW_NAME = "new_name",
            ACTION_GENERATE = "generate",
            ACTION_SET = "set",
            ACTION_GET = "get",
            NAME = "name",
            PASSWORD = "password",
            EMAIL = "email";

    private static final int ERROR_DIALOG_REQUEST = 9001, REQUEST_ACCOUNT = 2, OK = 0;


    public static void generateName(Context context, final NameListener listener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = preferences.getString(EMAIL, null), password = preferences.getString(PASSWORD, null);
        RetrofitRequest request = new RetrofitRequest(NAME, ACTION_GENERATE, email, password);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                if (result == OK) {
                    Log.d(TAG, "onResponse: generated name: " + response.get(NAME));
                    listener.onReturned(response.get(NAME));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onResponse: nick generation error! error: " + t.toString());
                listener.onFailed(t);
            }
        });
        request.makeRequest();
    }

    public static void setName(final String newName, Context context, final NameListener listener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = preferences.getString(EMAIL, null), password = preferences.getString(PASSWORD, null);
        RetrofitRequest request = new RetrofitRequest(NAME, ACTION_SET, email, password);
        request.putParam(NEW_NAME, newName);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                if (result == OK) {
                    Log.d(TAG, "onResponse: nick change to " + newName);
                    listener.onReturned(newName);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onResponse: nick change error! error: " + t.toString());
                listener.onFailed(t);
            }
        });
        request.makeRequest();
    }

    public static void getName(Context context, final NameListener listener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = preferences.getString(EMAIL, null), password = preferences.getString(PASSWORD, null);
        Log.d(TAG, "getName: " + email + " " + password);
        RetrofitRequest request = new RetrofitRequest(NAME, ACTION_GET, email, password);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                if (result == OK) {
                    Log.d(TAG, "onResponse: gotten name is " + response.get(NAME));
                    listener.onReturned(response.get(NAME));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: very bad ;( " + t.toString());
                listener.onFailed(t);
            }
        });
        request.makeRequest();
    }

    public interface NameListener {
        void onReturned(String name);

        void onFailed(Throwable t);
    }

}

