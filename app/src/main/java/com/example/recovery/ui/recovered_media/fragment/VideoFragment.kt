package com.example.recovery.ui.recovered_media.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.recovery.R
import com.example.recovery.databinding.FragmentVideoBinding
import com.example.recovery.delegate.viewBinding
import com.example.recovery.extension.gone
import com.example.recovery.extension.startActivity
import com.example.recovery.extension.visible
import com.example.recovery.extension.visibleIf
import com.example.recovery.model.FileModel
import com.example.recovery.ui.recovered_media.MediaViewModel
import com.example.recovery.ui.recovered_media.activity.ShowMediaActivity
import com.example.recovery.ui.recovered_media.adapter.RecoverVideoAdapter
import com.example.recovery.utils.Resources
import kotlinx.coroutines.launch

class VideoFragment : Fragment(R.layout.fragment_video) {
    private val viewModel by viewModels<MediaViewModel>()
    private val binding by viewBinding<FragmentVideoBinding>()
    private val recoverVideoAdapter by lazy {
        RecoverVideoAdapter {
            val item = it.tag as FileModel
            startActivity<ShowMediaActivity> { putExtra("filePath", item.file.absolutePath) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.videoImageSharedFlow.collect {
                    when (it) {
                        is Resources.Idle -> {}
                        is Resources.Progress -> binding.progressBar.visible()
                        is Resources.Success -> {
                            binding.animationView.visibleIf(it.data.isNullOrEmpty())
                            binding.progressBar.gone()
                            recoverVideoAdapter.submitList(it.data)
                            binding.rvVideo.adapter = recoverVideoAdapter

                        }
                    }
                }
            }
        }
    }
}