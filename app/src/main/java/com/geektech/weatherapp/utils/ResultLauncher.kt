package com.geektech.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner

class ResultLauncher(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    activityResultRegistry: ActivityResultRegistry,
    callback: (resultCode: ActivityResult) -> Unit,
) {

    private val registerActivityResult =
        activityResultRegistry.register(REGISTRY_KEY,
            lifecycleOwner,
            ActivityResultContracts.StartActivityForResult(),
            callback)

    fun openAppSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            registerActivityResult.launch(appSettingsIntent)

    }

    private companion object {
        private const val REGISTRY_KEY = "intent_settings"
    }
}