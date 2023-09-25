package com.example.recovery.ui.scanVideo.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.recovery.R
import com.example.recovery.databinding.ActivityShowScanVideoBinding
import com.example.recovery.extension.getCompactColor
import com.example.recovery.extension.getCompactDrawable
import com.example.recovery.extension.gone
import com.example.recovery.extension.toast
import com.example.recovery.extension.visible
import com.example.recovery.model.FileModel
import com.example.recovery.ui.scanVideo.ScanVideoViewModel
import com.example.recovery.ui.scanVideo.adapter.VideoAdapter
import com.example.recovery.utils.Resources
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.launch


class ShowScanVideoActivity : AppCompatActivity(), View.OnClickListener {

    private var recoverFileCounter = 0
    private val viewModel by viewModels<ScanVideoViewModel>()
    private val videoAdapter by lazy {
        VideoAdapter {
            if ((it.tag as FileModel).isSelected) recoverFileCounter++ else recoverFileCounter--
            binding.tvRecoverCounter.apply {
                text = "Recover($recoverFileCounter)"
                isEnabled = recoverFileCounter != 0
                binding.tvRecoverCounter.activateOrDeActivate()
            }
        }
    }

    private lateinit var binding: ActivityShowScanVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowScanVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initView()
        binding.tvRecoverCounter.isEnabled = false
    }

    private fun initToolbar() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.isRecoverProgressOn) {
                    finish()
                    return
                }

                toast("Wait until recover is complete")
            }

        })
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initView() {
        binding.tvRecoverCounter.setCharacterLists(TickerUtils.provideNumberList())
        binding.tvRecoverCounter.setOnClickListener(this)
        videoAdapter.submitList(ScanVideoViewModel.videoList.toMutableList())
        binding.rvVideo.adapter = videoAdapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.recoverVideoSharedFlow.collect {data->
                    when (data) {
                        is Resources.Idle -> {}
                        is Resources.Progress -> binding.progressBar.visible()
                        is Resources.Success -> {
                            binding.progressBar.gone()
                            if (data.data!!) {
                                toast("Video recover successfully")
                               val list =videoAdapter.currentList.map { it.apply { isSelected = false }}
                                videoAdapter.submitList(list.toMutableList())
                                videoAdapter.notifyDataSetChanged()
                            } else
                                toast("Something went wrong")
                        }
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvRecoverCounter -> {
                viewModel.recoverVideoFile(videoAdapter.currentList.filter { it.isSelected })
                recoverFileCounter = 0
                binding.tvRecoverCounter.apply {
                    text = "Recover($recoverFileCounter)"
                    isEnabled = false
                    activateOrDeActivate()
                }
            }
        }
    }

    private fun TickerView.activateOrDeActivate() {
        textColor = if (isEnabled)  getCompactColor(R.color.activateColor) else getCompactColor(R.color.deActivateColor)
        background =
            getCompactDrawable(if (isEnabled) R.drawable.back_active_stroke else R.drawable.back_inactive_stroke)
    }

}

