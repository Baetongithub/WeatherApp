package com.geektech.weatherapp.di

import com.geektech.weatherapp.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModules = module { viewModel { MainViewModel(get()) } }