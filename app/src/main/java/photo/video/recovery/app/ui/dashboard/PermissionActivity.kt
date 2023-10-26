package photo.video.recovery.app.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.github.hariprasanths.bounceview.BounceView
import photo.video.recovery.app.R
import photo.video.recovery.app.databinding.ActivityPermissionBinding
import photo.video.recovery.app.databinding.DialogPermissionPermanetlyBinding
import photo.video.recovery.app.extension.getCompactDrawable
import photo.video.recovery.app.extension.hasPermissions
import photo.video.recovery.app.extension.toast
import photo.video.recovery.app.utils.Constant

class PermissionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPermissionBinding

    private val permissionList =
        mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionRequest by lazy {
        permissionsBuilder(permissionList.first(), *permissionList.toTypedArray()).build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        onBackPressedDispatcher.addCallback { finish() }
        binding.ivClose.setOnClickListener(this)
        binding.btnAllow.setOnClickListener(this)

        BounceView.addAnimTo(binding.ivClose)
        BounceView.addAnimTo(binding.btnAllow)

        permissionRequest.addListener { result ->
            when {
                result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog()
                result.anyShouldShowRationale() -> toast("For a seamless app experience, kindly enable storage access permission")
                result.allGranted() -> onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivClose -> onBackPressedDispatcher.onBackPressed()
            R.id.btnAllow -> askForPermission()
        }
    }

    private fun askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                navigateToManageStoragePermissionScreen()
                return
            }
        } else if (!hasPermissions(permissionList)) {
            permissionRequest.send()
            return
        }

        onBackPressedDispatcher.onBackPressed()
    }


    @SuppressLint("SetTextI18n")
    private fun showPermanentlyDeniedDialog(isForManagerStorage: Boolean = false) {
        val binding = DialogPermissionPermanetlyBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        if (isForManagerStorage) {
            binding.tvPermissionMsg.text =
                "Storage access is crucial for the app to function effectively, so please grant the necessary permission."
        }
        dialog.window?.apply {
            setBackgroundDrawable(getCompactDrawable(R.drawable.back_dialog))
            setLayout((resources.displayMetrics.widthPixels * 0.85).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
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

    @SuppressLint("InlinedApi")
    private fun navigateToManageStoragePermissionScreen() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
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
        if (requestCode == 1001) {
            if (hasPermissions(permissionList)) {
                onBackPressedDispatcher.onBackPressed()
                return
            }

            toast("Please grant us permission for use this feature")
        } else {
            if (requestCode == Constant.REQUEST_CODE_MANAGE_STORAGE_PERMISSION) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        showPermanentlyDeniedDialog(true)
                        return
                    }
                }

                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}