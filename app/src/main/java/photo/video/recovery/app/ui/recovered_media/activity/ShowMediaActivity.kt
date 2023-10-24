package photo.video.recovery.app.ui.recovered_media.activity

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import photo.video.recovery.app.databinding.ActivityShowMediaBinding
import photo.video.recovery.app.extension.addBounceAnim
import photo.video.recovery.app.extension.isImageOrGifFile
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.extension.visible
import photo.video.recovery.app.ui.dashboard.DashboardActivity
import java.io.File

class ShowMediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initView()
    }

    private fun initToolbar() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


    }

    private fun initView() {
        val filePath = intent?.getStringExtra("filePath") ?: return
        val file = File(filePath)
        if (file.isImageOrGifFile()) {
            binding.toolbar.title = "Photo"
            binding.ivImage.visible()
            Glide.with(binding.ivImage)
                .load(file)
                .into(binding.ivImage)
        } else {
            binding.toolbar.title = "Video"
            binding.videoView.visible()

            val mc = MediaController(this@ShowMediaActivity)
            mc.setAnchorView(binding.videoView)
            mc.setMediaPlayer(binding.videoView)
            binding.videoView.setMediaController(mc)
            binding.videoView.setVideoURI(Uri.fromFile(file))
            binding.videoView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.videoView.isVisible)
            binding.videoView.resume()
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isVisible)
            binding.videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.apply {
            if (isVisible) {
                pause()
                stopPlayback()
            }
        }
    }
}