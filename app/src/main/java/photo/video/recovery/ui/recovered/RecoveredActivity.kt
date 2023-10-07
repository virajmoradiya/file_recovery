package photo.video.recovery.ui.recovered

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import photo.video.recovery.databinding.ActivityRecoveredBinding
import photo.video.recovery.extension.startActivity
import photo.video.recovery.model.FileModel
import photo.video.recovery.ui.recovered_media.activity.ShowMediaActivity
import photo.video.recovery.ui.recovered_media.adapter.RecoverImageAdapter
import photo.video.recovery.ui.recovered_media.adapter.RecoverVideoAdapter
import photo.video.recovery.utils.Constant
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import photo.video.recovery.extension.addBounceAnim
import photo.video.recovery.extension.isVideoFile
import photo.video.recovery.ui.dashboard.DashboardActivity

class RecoveredActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoveredBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoveredBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback { finish() }

        initView()

        startInAppReviewFlow()
    }

    private fun startInAppReviewFlow() {
        with(ReviewManagerFactory.create(this)) {
            requestReviewFlow()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        launchReviewFlow(
                            this@RecoveredActivity,
                            task.result
                        ).addOnCompleteListener { }
                    }
                }
        }
    }

    private fun initView() {

        binding.ivHome.addBounceAnim()
        binding.ivHome.setOnClickListener {
            startActivity<DashboardActivity> { }
            finish()
        }

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val fileList = Constant.recoveryFileList
        if (fileList.isEmpty())
            return


        val isVideo = fileList.first().file.isVideoFile()

        val noPhotos = "Number of ${if (isVideo) "Videos" else "Photos"} :"
        binding.txtNoPhotos.text = noPhotos
        binding.tvFileCount.text = " ${fileList.size}"
        binding.tvFolderPath.text =
            "Download/${Constant.APP_NAME}/${if (isVideo) Constant.VIDEO_FOLDER else Constant.IMAGE_FOLDER}"
        binding.tvSubTitle.text = "Recovered ${if (isVideo) "Videos" else "Photos"} details"
        binding.tvRecoveredTitle.text = "-: Recovered ${if (isVideo) "Videos" else "Photos"} :-"

        val fileAdapter = if (isVideo) RecoverVideoAdapter(this) else RecoverImageAdapter(this)
        fileAdapter.submitList(fileList)
        binding.rvResult.adapter = fileAdapter
    }

    override fun onClick(p0: View?) {
        val item = p0?.tag as FileModel?
        startActivity<ShowMediaActivity> { putExtra("filePath", item?.file?.absolutePath) }
    }
}