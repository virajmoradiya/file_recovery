package photo.video.recovery.ui.splash

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import photo.video.recovery.extension.alphaAnimation
import photo.video.recovery.extension.startActivity
import photo.video.recovery.ui.dashboard.DashboardActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    private val viewModel: SplashViewModel by viewModels()
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.isReadyForExit.value }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val animation = splashScreenView.alphaAnimation()

            val animatorSet = AnimatorSet()
            animatorSet.duration = 1500
            animatorSet.interpolator = AnticipateInterpolator()
            animatorSet.playTogether(animation)

            animatorSet.doOnEnd {
                splashScreenView.remove()
                startActivity<DashboardActivity> { }
                finish()
            }
            animatorSet.start()
        }

        super.onCreate(savedInstanceState)


    }



}