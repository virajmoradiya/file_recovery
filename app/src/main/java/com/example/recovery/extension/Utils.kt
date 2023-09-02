package com.example.recovery.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

internal fun Context.getCompactColor(color: Int):Int= ContextCompat.getColor(this,color)
internal fun Context.getCompactDrawable(drawable: Int):Drawable?= ContextCompat.getDrawable(this,drawable)

internal fun AppCompatActivity.changeStatusBarColor(color: Int) {
    window.statusBarColor = getCompactColor(color)
}