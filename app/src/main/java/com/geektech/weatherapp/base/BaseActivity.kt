package com.geektech.weatherapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    val vb get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = viewBinding()
        setContentView(vb.root)
        checkConnectionState()
        uiFunc()
        liveData()
    }

    abstract fun viewBinding(): VB
    abstract fun uiFunc()
    abstract fun liveData()
    abstract fun checkConnectionState()
}