package com.geektech.weatherapp.extensions

import android.view.View
import android.view.View.*
import com.google.android.material.snackbar.Snackbar

var View.visible: Boolean
    get() = visibility == VISIBLE
    set(value) {
        if (value) VISIBLE else INVISIBLE
    }

var View.invisible: Boolean
    get() = visibility == INVISIBLE
    set(value) {
        if (value) INVISIBLE else VISIBLE
    }

var View.gone: Boolean
    get() = visibility == GONE
    set(value) {
        if (value) GONE else VISIBLE
    }

fun View.snackBar(txt: String) {
    Snackbar.make(this, txt, Snackbar.LENGTH_SHORT).show()
}