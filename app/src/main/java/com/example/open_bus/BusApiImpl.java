package com.example.open_bus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.xml.sax.helpers.XMLReaderFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BusApiImpl {
    private static final String BASE_URL = "http://ws.bus.go.kr";

    public static BusApi getApiService() {
        return getInstance().create(BusApi.class);
    }

    private static Retrofit getInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
