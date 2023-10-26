package photo.video.recovery.app.ui.dashboard

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import photo.video.recovery.app.R
import photo.video.recovery.app.databinding.ActivityDashboardBinding
import photo.video.recovery.app.databinding.DialogExitBinding
import photo.video.recovery.app.extension.addBounceAnim
import photo.video.recovery.app.extension.getCompactDrawable
import photo.video.recovery.app.extension.hasPermissions
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.extension.startActivityForResult
import photo.video.recovery.app.extension.toast
import photo.video.recovery.app.ui.recovered_media.activity.RecoveredFileActivity
import photo.video.recovery.app.ui.scanImage.activity.ImageScanActivity
import photo.video.recovery.app.ui.scanVideo.activity.VideoScanActivity
import photo.video.recovery.app.ui.settings.SettingActivity
import photo.video.recovery.app.utils.Constant
import photo.video.recovery.app.utils.Constant.isAppHaveUpdate
import photo.video.recovery.app.utils.InAppUpdate


class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }
    private val permissionList =
        mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val inAppUpdate by lazy { InAppUpdate(this) }

    private var view: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intiView()
        analyticsFirebase()

        if (isAppHaveUpdate) {
            inAppUpdate.startAppUpdate()
        }
    }

    private fun analyticsFirebase() {
        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance()
    }

    private fun intiView() {
        onBackPressedDispatcher.addCallback {
            showExitDialog()
        }

        binding.tvVideo.addBounceAnim()
        binding.tvRecoveredFile.addBounceAnim()
        binding.tvSetting.addBounceAnim()
        binding.tvImage.addBounceAnim()

        binding.tvVideo.setOnClickListener(this)
        binding.tvRecoveredFile.setOnClickListener(this)
        binding.tvSetting.setOnClickListener(this)
        binding.tvImage.setOnClickListener(this)

    }

    private fun showExitDialog() {
        val dialogBinding = DialogExitBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(getCompactDrawable(R.drawable.back_dialog))
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.85).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    override fun onClick(p0: View?) {
        view = p0
        when (view?.id) {
            R.id.tvImage -> navigateToOtherScreen()
            R.id.tvVideo -> navigateToOtherScreen()
            R.id.tvRecoveredFile -> navigateToOtherScreen()
            R.id.tvSetting -> startActivity<SettingActivity> { }

        }
    }

    private fun navigateToOtherScreen() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                startActivityForResult<PermissionActivity> (Constant.REQUEST_CODE_PERMISSION){ }
                return
            }
        } else if (!hasPermissions(permissionList)) {
            startActivityForResult<PermissionActivity>(Constant.REQUEST_CODE_PERMISSION) { }
            return
        }

        when (view?.id) {
            R.id.tvImage -> startActivity<ImageScanActivity> { }
            R.id.tvVideo -> startActivity<VideoScanActivity> { }
            R.id.tvRecoveredFile -> startActivity<RecoveredFileActivity> { }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode, resultCode, data)
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                toast("For a seamless app experience, kindly enable storage access permission")
                return
            }
        } else if (!hasPermissions(permissionList)) {
            toast("For a seamless app experience, kindly enable storage access permission")
            return
        }

        when (view?.id) {
            R.id.tvImage -> startActivity<ImageScanActivity> { }
            R.id.tvVideo -> startActivity<VideoScanActivity> { }
            R.id.tvRecoveredFile -> startActivity<RecoveredFileActivity> { }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate.onDestroy()
    }


}
