package com.example.heart_dog;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hubert on 2016-02-02.
 */
public interface ApiInterface {
    //@GET("/data/2.5/weather?lat={lat}&lon={lon}&units={units}&appid=cf4c41ffa55d4ef37c9d73efede7fa12")
    //Call<Repo> repo(@Path("lat") String lat, @Path("lon") String lon);

    @GET("/data/2.5/weather")
    Call<Repo> repo(@Query("units") String units, @Query("appid") String appid, @Query("lat") double lat, @Query("lon") double lon);
}

