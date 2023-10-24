package photo.video.recovery.app.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import photo.video.recovery.app.R
import photo.video.recovery.app.extension.isNetworkAvailable
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.ui.dashboard.DashboardActivity
import photo.video.recovery.app.utils.Constant.isAppHaveUpdate
import photo.video.recovery.app.utils.Constant.isFlexibleUpdate

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val DELAY_TIME = 3600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        lifecycleScope.launch {
            delay(DELAY_TIME)
            getFirebase()
        }
        lifecycleScope.launch {
            findViewById<RoundCornerProgressBar>(R.id.progressBar).setMax(DELAY_TIME.toInt())
            repeat((DELAY_TIME/100).toInt()) {
                delay((DELAY_TIME/(DELAY_TIME/100)))
                findViewById<RoundCornerProgressBar>(R.id.progressBar).apply {
                    val progress= (this.getProgress() + (DELAY_TIME/(DELAY_TIME/100)))
                    setProgress(progress)
                }
            }
        }
    }

    private fun getFirebase() {
        if (!isNetworkAvailable()) {
            navigateToMain()
            return
        }
        FirebaseFirestore.getInstance().collection("app-data").get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    val jsonObject = JSONObject(document.data["data"] as Map<String, String>)
                    isFlexibleUpdate = jsonObject.get("is_flexible_update") as Boolean
                    isAppHaveUpdate = jsonObject.get("is_app_have_update") as Boolean
                    navigateToMain()
                }
            }.addOnFailureListener {
                navigateToMain()
            }
    }

    private fun navigateToMain() {
        startActivity<DashboardActivity> { }
        finish()
    }


}