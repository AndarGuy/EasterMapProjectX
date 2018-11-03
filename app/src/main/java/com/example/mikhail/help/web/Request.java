package com.example.mikhail.help.web;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Request {
    @FormUrlEncoded
    @POST("/")
    Call<Object> performPostCall(@FieldMap HashMap<String, String> postDataParams);
}