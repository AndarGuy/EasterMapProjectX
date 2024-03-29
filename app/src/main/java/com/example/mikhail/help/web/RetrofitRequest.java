package com.example.mikhail.help.web;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static final String TAG = "RetrofitRequest";

    private final String RESULT = "result", EMAIL = "email", PASSWORD = "password";
    private String server = "http://209.97.185.108";
    private String action, nextAction;
    private String email, password;
    private HashMap<String, String> postDataParams = new HashMap<>();
    private RequestListener listener;
    private Gson gson = new GsonBuilder().setLenient().create();
    private OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .baseUrl(server)
            .build();
    private Request req = retrofit.create(Request.class);

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
                try {
                    HashMap<String, String> map = gson.fromJson(gson.toJson(response.body()), HashMap.class);
                    Integer result = Integer.valueOf(map.get(RESULT));
                    listener.onResponse(call, map, result);
                } catch (Exception e) {
                    Log.d(TAG, "ERROR: " + e.getClass().getName() + " BODY: " + response.body());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                listener.onFailure(call, t);
            }
        });
    }

    public Answer makeExecuteRequest() {
        putParam(action, nextAction);
        putParam(EMAIL, email);
        putParam(PASSWORD, password);

        Call<Object> call = req.performPostCall(postDataParams);
        try {
            Response<Object> response = call.execute();
            HashMap<String, String> map = gson.fromJson(gson.toJson(response.body()), HashMap.class);
            Integer result = Integer.valueOf(map.get(RESULT));
            return new Answer(call, map, result);
        } catch (IOException e) {
            Log.d(TAG, "makeExecuteRequest: execute request error!");
            return null;
        }
    }

    public void putParam(String key, String value) {
        if (key != null) {
            if (value == null) value = "null";
            this.postDataParams.put(key, value);
        }
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setListener(RequestListener listener) {
        this.listener = listener;
    }
}