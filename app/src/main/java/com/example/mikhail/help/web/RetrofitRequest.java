package com.example.mikhail.help.web;

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

    final static String
            ACTION = "action",
            RESULT = "result";

    String server = "https://mfomenko123.000webhostapp.com";
    String action = "test";
    HashMap<String, String> postDataParams = new HashMap<>();
    RequestListener listener;

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(server)
            .build();
    Request req = retrofit.create(Request.class);

    public void makeRequest() {
        postDataParams.put(ACTION, action);
        Call<Object> call = req.performPostCall(postDataParams);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                HashMap<String, Double> map = gson.fromJson(response.body().toString(), HashMap.class);
                Integer result = map.get(RESULT).intValue();
                listener.onResponse(call, map, result);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                listener.onFailure(call, t);
            }
        });
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