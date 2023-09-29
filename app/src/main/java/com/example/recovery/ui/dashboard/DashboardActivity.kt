package com.example.recovery.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.recovery.R
import com.example.recovery.databinding.ActivityDashboardBinding
import com.example.recovery.databinding.DialogExitBinding
import com.example.recovery.databinding.DialogPermissionPermanetlyBinding
import com.example.recovery.extension.addBounceAnim
import com.example.recovery.extension.getCompactDrawable
import com.example.recovery.extension.hasPermissions
import com.example.recovery.extension.startActivity
import com.example.recovery.extension.toast
import com.example.recovery.ui.recovered_media.activity.RecoveredFileActivity
import com.example.recovery.ui.scanImage.activity.ImageScanActivity
import com.example.recovery.ui.scanVideo.activity.VideoScanActivity
import com.example.recovery.ui.settings.SettingActivity
import com.example.recovery.utils.Constant
import com.example.recovery.utils.InAppUpdate
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder


class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }
    private val permissionList = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val permissionRequest by lazy {
        permissionsBuilder(permissionList.first(), *permissionList.toTypedArray()).build()
    }

    private val inAppUpdate by lazy { InAppUpdate(this) }

    private var view: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intiView()
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

        permissionRequest.addListener { result ->
            when {
                result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog()
                result.anyShouldShowRationale() -> toast("Please grant us permission for use this feature")
                result.allGranted() -> navigateToOtherScreen()
            }
        }
    }

    private fun showExitDialog() {
        val dialogBinding = DialogExitBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(getCompactDrawable(R.drawable.back_dialog))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showPermanentlyDeniedDialog(isForManagerStorage: Boolean = false) {
        val binding = DialogPermissionPermanetlyBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        if (isForManagerStorage) {
            binding.tvPermissionMsg.text =
                getString(R.string.app_need_the_manage_storage_permission_to_provide_necessary_services)
        }
        dialog.window?.apply {
            setBackgroundDrawable(getCompactDrawable(R.drawable.back_dialog))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        binding.tvClose.setOnClickListener { dialog.dismiss() }
        binding.tvGoToSetting.setOnClickListener {
            if (isForManagerStorage) {
                navigateToManageStoragePermissionScreen()
                dialog.dismiss()
                return@setOnClickListener
            }
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 1001)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onClick(p0: View?) {
        view = p0
        when (view?.id) {
            R.id.tvImage -> if (SDK_INT >= Build.VERSION_CODES.R) navigateToOtherScreen() else permissionRequest.send()
            R.id.tvVideo -> if (SDK_INT >= Build.VERSION_CODES.R) navigateToOtherScreen() else permissionRequest.send()
            R.id.tvRecoveredFile -> if (SDK_INT >= Build.VERSION_CODES.R) navigateToOtherScreen() else permissionRequest.send()
            R.id.tvSetting -> startActivity<SettingActivity> { }

        }
    }

    private fun navigateToOtherScreen() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                navigateToManageStoragePermissionScreen()
                return
            }
        }
        when (view?.id) {
            R.id.tvImage -> startActivity<ImageScanActivity> { }
            R.id.tvVideo -> startActivity<VideoScanActivity> { }
            R.id.tvRecoveredFile -> startActivity<RecoveredFileActivity> { }
        }
    }

    @SuppressLint("InlinedApi")
    private fun navigateToManageStoragePermissionScreen() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data =
                Uri.parse(String.format("package:%s", applicationContext.packageName))
            startActivityForResult(intent, Constant.REQUEST_CODE_MANAGE_STORAGE_PERMISSION)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, Constant.REQUEST_CODE_MANAGE_STORAGE_PERMISSION)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (hasPermissions(permissionList)) {
                navigateToOtherScreen()
                return
            }

            toast("Please grant us permission for use this feature")
        } else {
            if (requestCode == Constant.REQUEST_CODE_MANAGE_STORAGE_PERMISSION && resultCode == RESULT_OK) {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        showPermanentlyDeniedDialog(true)
                        return
                    }
                }

                navigateToOtherScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate.onDestroy()
    }


}
