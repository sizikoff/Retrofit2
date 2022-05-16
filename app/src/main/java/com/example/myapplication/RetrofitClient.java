package com.example.myapplication;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit ourInstance;

    public static Retrofit getInstance(){
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        OkHttpClient client = builder.build();
        ourInstance = new Retrofit.Builder()
                .baseUrl("http://demo7877231.mockable.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return ourInstance;
    }

    private RetrofitClient(){

    }
}
