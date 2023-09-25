package com.example.recovery.ui.recovered_media.activity

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.recovery.databinding.ActivityShowMediaBinding
import com.example.recovery.extension.isImageOrGifFile
import com.example.recovery.extension.visible
import java.io.File


class ShowMediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShowMediaBinding.inflate(layoutInflater)
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

            binding.videoView.setVideoURI(Uri.fromFile(file))
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                binding.videoView.start()
            }
        }
    }
}