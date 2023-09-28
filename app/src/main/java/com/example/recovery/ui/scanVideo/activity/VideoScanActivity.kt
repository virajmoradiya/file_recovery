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
import com.example.recovery.databinding.ActivityVideoScanBinding
import com.example.recovery.extension.invisible
import com.example.recovery.extension.startActivity
import com.example.recovery.extension.visibleIf
import com.example.recovery.ui.scanVideo.ScanVideoViewModel
import com.example.recovery.utils.Resources
import com.robinhood.ticker.TickerUtils
import kotlinx.coroutines.launch

class VideoScanActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityVideoScanBinding
    private val viewModel by viewModels<ScanVideoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.waveView.startRippleAnimation()
        binding.tvTapOnStartScan.setCharacterLists(TickerUtils.provideNumberList())

        initToolbar()
        initView()
    }

    private fun initView() {
        binding.ivScanVideo.setOnClickListener(this)
        binding.tvTapOnStartScan.setOnClickListener(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.scanVideoSharedFlow.collect { uiState ->
                    when (uiState) {
                        is Resources.Success -> startActivity<ShowScanVideoActivity> { finish() }

                        is Resources.Progress -> binding.tvTapOnStartScan.text =
                            "${uiState.count}  Video "

                        is Resources.Idle -> if (!uiState.message.isNullOrBlank()) {
                            binding.tvTapOnStartScan.text = "Preparing..."
                        }

                        is Resources.Error ->  {
                            binding.tvTapOnStartScan.isEnabled = false
                            binding.tvTapOnStartScan.text = "There is no videos found"
                            binding.tvScanPath.invisible()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.scanPathSharedFlow.collect {
                    binding.tvScanPath.visibleIf(it.isNotEmpty())
                    binding.tvScanPath.text = it
                }
            }
        }

        viewModel.startScanning()
    }


    private fun initToolbar() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                finish()
            }

        })
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivScanVideo, R.id.tvTapOnStartScan -> viewModel.startScanning()
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseData()
    }

    private fun releaseData() {
        ScanVideoViewModel.videoList.clear()
    }
}