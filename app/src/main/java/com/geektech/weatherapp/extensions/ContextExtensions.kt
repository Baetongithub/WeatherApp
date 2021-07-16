package com.geektech.weatherapp.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

fun Context.toast(txt: String) = Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()

fun Int.convertDate(): String {
    val date = Date(this * 1000L)
    val sdf = SimpleDateFormat("HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("GMT+6")
    return sdf.format(date)
}

fun Context.snackBar(view: View, txt: String) {
    Snackbar.make(this, view, txt, Snackbar.LENGTH_SHORT).show()
}