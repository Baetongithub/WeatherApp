package com.geektech.weatherapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.geektech.weatherapp.R

class ProgrDialog(activity: Activity, endGPS: () -> Unit) {

    private val dialog: Dialog

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    init {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(view)
        builder.setCancelable(false)
        builder.setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            endGPS()
            dismiss()
        }
        dialog = builder.create()
    }
}