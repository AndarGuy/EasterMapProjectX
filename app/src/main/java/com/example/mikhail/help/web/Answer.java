package com.example.mikhail.help.web;

import java.util.HashMap;

import retrofit2.Call;

public class Answer {
    private Integer result;
    private HashMap<String, String> response;
    private Call<Object> call;

    Answer(Call<Object> call, HashMap<String, String> response, Integer result) {
        this.call = call;
        this.response = response;
        this.result = result;
    }

    public Call<Object> getCall() {
        return call;
    }

    public HashMap<String, String> getResponse() {
        return response;
    }

    public Integer getResult() {
        return result;
    }
}
