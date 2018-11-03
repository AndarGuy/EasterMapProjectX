package com.example.mikhail.help.web;

import java.util.HashMap;

import retrofit2.Call;

public interface RequestListener {
    void onResponse(Call<Object> call, HashMap<String, Double> response, Integer result);

    void onFailure(Call<Object> call, Throwable t);
}
