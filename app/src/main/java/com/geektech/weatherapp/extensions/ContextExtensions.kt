package com.geektech.weatherapp.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.*

fun Context.toast(txt: String) = Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()

fun Int.convertDate(): String {
    val t: String
    val cal = Calendar.getInstance(Locale.ROOT)
    cal.timeInMillis = (this * 1000).toLong()
    val hours = cal[Calendar.HOUR_OF_DAY]
    val minutes = cal[Calendar.MINUTE]
    val seconds = cal[Calendar.SECOND]
    t = "$hours:$minutes:$seconds"
    return t
}

fun Context.snackBar(view: View, txt: String) {
    Snackbar.make(this, view, txt, Snackbar.LENGTH_SHORT).show()
}