package com.example.recovery.ui.recovered

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.recovery.databinding.ActivityRecoveredBinding
import com.example.recovery.extension.isImageOrGifFile
import com.example.recovery.extension.startActivity
import com.example.recovery.model.FileModel
import com.example.recovery.ui.recovered_media.activity.ShowMediaActivity
import com.example.recovery.ui.recovered_media.adapter.RecoverImageAdapter
import com.example.recovery.ui.recovered_media.adapter.RecoverVideoAdapter
import com.example.recovery.utils.Constant
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
                    } else {
                        Log.w(
                            "TAG",
                            "There was a problem requesting the review flow ${task.exception}"
                        )
                    }
                }
        }
    }

    private fun initView() {

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val fileList = Gson().fromJson<List<FileModel>>(
            intent.getStringExtra("fileList"),
            object : TypeToken<List<FileModel>>() {}.type
        )
        if (fileList.isEmpty())
            return


        val isImage = fileList.first().file.isImageOrGifFile()

        val noPhotos = "Number of ${if (isImage) "Photos" else "Videos"} :"
        binding.txtNoPhotos.text = noPhotos
        binding.tvFileCount.text = fileList.size.toString()
        binding.tvFolderPath.text =
            "Download/${Constant.APP_NAME}/${if (isImage) Constant.IMAGE_FOLDER else Constant.VIDEO_FOLDER}"
        binding.tvSubTitle.text = "Recovered ${if (isImage) "Photos" else "Videos"} details"
        binding.tvRecoveredTitle.text = "-: Recovered ${if (isImage) "Photos" else "Videos"} :-"

        val fileAdapter = if (isImage) RecoverImageAdapter(this) else RecoverVideoAdapter(this)
        fileAdapter.submitList(fileList)
        binding.rvResult.adapter = fileAdapter
    }

    override fun onClick(p0: View?) {
        val item = p0?.tag as FileModel?
        startActivity<ShowMediaActivity> { putExtra("filePath", item?.file?.absolutePath) }
    }
}