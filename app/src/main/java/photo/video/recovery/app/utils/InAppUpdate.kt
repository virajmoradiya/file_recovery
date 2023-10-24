package photo.video.recovery.app.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdate(val parentActivity: Activity) : InstallStateUpdatedListener {

    private var appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(parentActivity)
    private val REQUEST_CODE_APP_UPDATE = 500

    private var currentType = AppUpdateType.FLEXIBLE

    fun startAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (Constant.isFlexibleUpdate) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startUpdate(info, AppUpdateType.FLEXIBLE)
                    }
                }else{
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdate(info, AppUpdateType.IMMEDIATE)
                    }
                }
            }
        }
        appUpdateManager.registerListener(this)
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        try {
            currentType = type
            appUpdateManager.startUpdateFlowForResult(info, type, parentActivity, REQUEST_CODE_APP_UPDATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_APP_UPDATE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                Log.i("APP_UPDATE", "onActivityResult: App need to be update")
            }
        }
    }

    private fun flexibleUpdateDownloadCompleted() {
        Snackbar.make(
                parentActivity.findViewById(android.R.id.content),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(Color.WHITE)
            show()
        }
    }

    fun onDestroy() {
        try {
            if (!Constant.isAppHaveUpdate) {
                return
            }
            appUpdateManager.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleUpdateDownloadCompleted()
        }
    }

}