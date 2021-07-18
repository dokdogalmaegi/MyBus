package com.example.open_bus;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BusApi {
    @GET("/api/rest/arrive/getArrInfoByRouteAll?ServiceKey=aImTjf1DEi1Qvanrayuy6CMD3jtXpChZNa4XvJuYjDFt0qKJKnoEEKcBCN6ffTW3tfNiU9x8QCP%2BQcGjm2Ls%2Bw%3D%3D&busRouteId=100100082")
    Call<String> get507();
}
