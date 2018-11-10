package com.example.mikhail.help.web;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static final String TAG = "RetrofitRequest";

    final String RESULT = "result", EMAIL = "email", PASSWORD = "password";
    String server = "https://mfomenko123.000webhostapp.com";
    String action, nextAction;
    String email, password;
    HashMap<String, String> postDataParams = new HashMap<>();
    RequestListener listener;
    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(server)
            .build();
    Request req = retrofit.create(Request.class);

    public RetrofitRequest(String action) {
        this.action = action;
    }

    public RetrofitRequest(String action, String nextAction) {
        this.action = action;
        this.nextAction = nextAction;
    }

    public RetrofitRequest(String action, String email, String password) {
        this.action = action;
        this.email = email;
        this.password = password;
    }

    public RetrofitRequest(String action, String nextAction, String email, String password) {
        this.action = action;
        this.nextAction = nextAction;
        this.email = email;
        this.password = password;
    }

    public void makeRequest() {
        putParam(action, nextAction);
        putParam(EMAIL, email);
        putParam(PASSWORD, password);

        Call<Object> call = req.performPostCall(postDataParams);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                String body = response.body().toString()
                        .replaceAll("=", "=" + '"')
                        .replace("}", '"' + "}")
                        .replaceAll(", ", '"' + ", ");
                try {
                    HashMap<String, String> map = gson.fromJson(body, HashMap.class);
                    Integer result = Integer.valueOf(map.get(RESULT));
                    listener.onResponse(call, map, result);
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: " + body, e);
                    listener.onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                listener.onFailure(call, t);
            }
        });
    }

    public void putParam(String key, String value) {
        if (key != null) {
            if (value == null) value = "null";
            this.postDataParams.put(key, value);
        }
    }

    public void setListener(RequestListener listener) {
        this.listener = listener;
    }
}