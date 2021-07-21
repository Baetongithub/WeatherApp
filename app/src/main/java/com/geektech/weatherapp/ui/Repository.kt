package com.geektech.weatherapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.geektech.weatherapp.BuildConfig.API_KEY
import com.geektech.weatherapp.data.model.MainWeather
import com.geektech.weatherapp.data.network.Resource
import com.geektech.weatherapp.data.network.WeatherAPI
import kotlinx.coroutines.Dispatchers

class Repository(private val weatherAPI: WeatherAPI) {
    fun getMainWeather(cityName: String?, units: String): LiveData<Resource<MainWeather?>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            val response = weatherAPI.getMainWeather(cityName!!, API_KEY, units)
            emit(
                if (response.isSuccessful) Resource.success(response.body())
                else Resource.error(response.message(), response.body(), response.code())
            )
        }
}