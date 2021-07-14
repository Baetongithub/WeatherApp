package com.geektech.weatherapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.geektech.weatherapp.base.BaseViewModel
import com.geektech.weatherapp.model.MainWeather
import com.geektech.weatherapp.network.Resource

class MainViewModel(private val repository: Repository) : BaseViewModel() {
    val loading = MutableLiveData<Boolean>()

    fun getWeather(cityName: String, units: String): LiveData<Resource<MainWeather?>> {
        return repository.getMainWeather(cityName, units)
    }
}