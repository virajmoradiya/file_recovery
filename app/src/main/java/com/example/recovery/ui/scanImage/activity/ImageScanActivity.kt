package com.example.recovery.ui.scanImage.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.recovery.R
import com.example.recovery.databinding.ActivityImageScanBinding
import com.example.recovery.extension.startActivity
import com.example.recovery.extension.visibleIf
import com.example.recovery.ui.scanImage.ScanImageViewModel
import com.example.recovery.ui.scanVideo.activity.ShowScanVideoActivity
import com.example.recovery.utils.Resources
import com.robinhood.ticker.TickerUtils
import kotlinx.coroutines.launch

class ImageScanActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityImageScanBinding
    private val viewModel by viewModels<ScanImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.waveView.startRippleAnimation()
        binding.tvTapOnStartScan.setCharacterLists(TickerUtils.provideNumberList())

        initToolbar()
        initView()
    }

    private fun initView() {
        binding.ivScanImage.setOnClickListener(this)
        binding.tvTapOnStartScan.setOnClickListener(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.scanImageSharedFlow.collect { uiState ->
                    when (uiState) {
                        is Resources.Success -> startActivity<ShowScanImageActivity> { finish() }

                        is Resources.Progress -> binding.tvTapOnStartScan.text =
                            "${uiState.count}  Images "

                        is Resources.Idle -> if (!uiState.message.isNullOrBlank()) {
                            binding.tvTapOnStartScan.text = "Preparing..."
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
            R.id.ivScanImage, R.id.tvTapOnStartScan -> viewModel.startScanning()
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseData()
    }

    private fun releaseData() {
        ScanImageViewModel.imageList.clear()
    }
}