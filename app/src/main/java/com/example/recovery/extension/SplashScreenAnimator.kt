package com.example.recovery.extension

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.view.View
import androidx.core.splashscreen.SplashScreenViewProvider

fun SplashScreenViewProvider.slideUpAnimation(): ObjectAnimator {
    return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -view.height.toFloat())
}

fun SplashScreenViewProvider.slideLeftAnimation(): ObjectAnimator {
    return ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -view.width.toFloat())
}

fun SplashScreenViewProvider.scaleXAnimation(): ObjectAnimator {
    return ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0f)
}

fun SplashScreenViewProvider.scaleXYAnimation(): ObjectAnimator {
    val path = Path()
    path.moveTo(1.0f, 1.0f)
    path.lineTo(0f, 0f)

    return ObjectAnimator.ofFloat(view, View.SCALE_X, View.SCALE_Y, path)
}

internal fun SplashScreenViewProvider.alphaAnimation(): ObjectAnimator {
    return ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
}