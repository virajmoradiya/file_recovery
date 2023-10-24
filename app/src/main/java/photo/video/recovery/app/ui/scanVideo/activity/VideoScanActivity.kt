package photo.video.recovery.app.ui.scanVideo.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import photo.video.recovery.app.R
import photo.video.recovery.app.databinding.ActivityVideoScanBinding
import photo.video.recovery.app.extension.invisible
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.extension.visibleIf
import photo.video.recovery.app.ui.scanVideo.ScanVideoViewModel
import photo.video.recovery.app.utils.Resources
import com.robinhood.ticker.TickerUtils
import kotlinx.coroutines.launch
import photo.video.recovery.app.extension.visible

class VideoScanActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityVideoScanBinding
    private val viewModel by viewModels<ScanVideoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTapOnStartScan.setCharacterLists(TickerUtils.provideNumberList())

        initToolbar()
        initView()
    }

    private fun initView() {

        binding.btnBackToHome.setOnClickListener(this)
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

                        is Resources.Error -> {
                            binding.tvTapOnStartScan.isEnabled = false
                            binding.tvTapOnStartScan.text = "Sorry! No videos found"
                            binding.tvScanPath.invisible()
                            binding.btnBackToHome.visible()
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
            R.id.tvTapOnStartScan -> viewModel.startScanning()
            R.id.btnBackToHome -> onBackPressedDispatcher.onBackPressed()
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