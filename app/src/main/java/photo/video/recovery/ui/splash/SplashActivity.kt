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
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import photo.video.recovery.extension.alphaAnimation
import photo.video.recovery.extension.isNetworkAvailable
import photo.video.recovery.extension.startActivity
import photo.video.recovery.ui.dashboard.DashboardActivity
import photo.video.recovery.utils.Constant.isFlexibleUpdate
import photo.video.recovery.utils.Constant.isAppHaveUpdate

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

            }
            animatorSet.start()
        }

        super.onCreate(savedInstanceState)

        getFirebase()
    }

    private fun getFirebase() {
        if (!isNetworkAvailable()) {
            viewModel.isReadyForExit.tryEmit(false)
            return
        }
        FirebaseFirestore.getInstance().collection("app-data").get().addOnSuccessListener { querySnapshot ->
            querySnapshot.forEach { document ->
                val jsonObject = JSONObject(document.data["data"] as Map<String, String>)
                isFlexibleUpdate = jsonObject.get("is_flexible_update") as Boolean
                isAppHaveUpdate = jsonObject.get("is_app_have_update") as Boolean
                viewModel.isReadyForExit.tryEmit(false)
            }
        }.addOnFailureListener {
            viewModel.isReadyForExit.tryEmit(false)
        }
    }



}