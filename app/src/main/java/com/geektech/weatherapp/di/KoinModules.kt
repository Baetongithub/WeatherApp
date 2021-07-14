package com.geektech.weatherapp.di

import com.geektech.weatherapp.network.networkModules

val koinModules = listOf(
    networkModules,
    repoModule,
    viewModules
)