package com.example.recovery.ui.splash

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recovery.extension.alphaAnimation
import com.example.recovery.extension.startActivity
import com.example.recovery.ui.dashboard.DashboardActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TIMER_ANIMATION: Long = 1500
    }

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.isReadyForExit.value }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val animation = splashScreenView.alphaAnimation()

            val animatorSet = AnimatorSet()
            animatorSet.duration = TIMER_ANIMATION
            animatorSet.interpolator = AnticipateInterpolator()
            animatorSet.playTogether(animation)

            animatorSet.doOnEnd {
                splashScreenView.remove()
                toMainActivity()
            }
            animatorSet.start()
        }

        super.onCreate(savedInstanceState)


    }


    private fun toMainActivity() {
        startActivity<DashboardActivity> { }
        finish()
    }
}