package com.geektech.weatherapp.di

import com.geektech.weatherapp.data.network.networkModules

val koinModules = listOf(
    networkModules,
    repoModule,
    viewModules
)