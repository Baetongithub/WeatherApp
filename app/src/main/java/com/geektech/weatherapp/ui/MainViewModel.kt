package com.geektech.weatherapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geektech.weatherapp.data.model.MainWeather
import com.geektech.weatherapp.data.network.Resource

class MainViewModel(private val repository: Repository) : ViewModel() {
    val loading = MutableLiveData<Boolean>()

    fun getWeather(cityName: String, units: String): LiveData<Resource<MainWeather?>> {
        return repository.getMainWeather(cityName, units)
    }
}