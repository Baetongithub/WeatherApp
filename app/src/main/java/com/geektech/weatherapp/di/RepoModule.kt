package com.geektech.weatherapp.di

import com.geektech.weatherapp.ui.Repository
import org.koin.dsl.module

val repoModule = module { single { Repository(get()) } }