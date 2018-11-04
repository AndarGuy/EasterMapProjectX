package com.example.mikhail.help.web;

import android.content.Intent;
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

    public RetrofitRequest(String action) {
        this.action = action;
    }

    private static final String TAG = "RetrofitRequest";

    final static String
            ACTION = "action",
            RESULT = "result";

    String server = "https://mfomenko123.000webhostapp.com";
    String action = "test", nextAction = "";
    HashMap<String, String> postDataParams = new HashMap<>();
    RequestListener listener;

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(server)
            .build();
    Request req = retrofit.create(Request.class);

    public void makeRequest() {
        postDataParams.put(action, nextAction);
        Call<Object> call = req.performPostCall(postDataParams);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                String body = response.body().toString()
                        .replace("{", "{" + '"')
                        .replaceAll("=", '"' + "=" + '"')
                        .replace("}", '"' + "}")
                        .replaceAll(", ", '"' + ", " + '"');
                try {
                    HashMap<String, String> map = gson.fromJson(body, HashMap.class);
                    Integer result = Integer.valueOf(map.get(RESULT).toString());
                    listener.onResponse(call, map, result);
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + response.body().toString() + " " + body);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                listener.onFailure(call, t);
            }
        });
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public String getServer() {
        return server;
    }

    public String getAction() {
        return action;
    }

    public void putParam(String key, String value) {
        this.postDataParams.put(key, value);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setListener(RequestListener listener) {
        this.listener = listener;
    }
}