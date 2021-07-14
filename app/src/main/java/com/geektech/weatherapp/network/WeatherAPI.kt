package com.geektech.weatherapp.network

import com.geektech.weatherapp.model.MainWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather")
    suspend fun getMainWeather(
        @Query("q")
        q: String,
        @Query("appid")
        apiKey: String,
        @Query("units")
        units: String
    ): Response<MainWeather>
}