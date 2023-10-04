package photo.video.recovery.ui.scanVideo.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import photo.video.recovery.R
import photo.video.recovery.databinding.ActivityShowScanVideoBinding
import photo.video.recovery.extension.getCompactColor
import photo.video.recovery.extension.getCompactDrawable
import photo.video.recovery.extension.gone
import photo.video.recovery.extension.startActivity
import photo.video.recovery.extension.toast
import photo.video.recovery.extension.visible
import photo.video.recovery.model.FileModel
import photo.video.recovery.ui.recovered.RecoveredActivity
import photo.video.recovery.ui.scanVideo.ScanVideoViewModel
import photo.video.recovery.ui.scanVideo.adapter.VideoAdapter
import photo.video.recovery.utils.Resources
import com.google.gson.Gson
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.delay
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

    private val progressDialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.layout_progress_dialog)
            window?.setBackgroundDrawable(getCompactDrawable(R.drawable.back_dialog))
            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
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
                viewModel.recoverVideoSharedFlow.collect { data ->
                    when (data) {
                        is Resources.Idle -> {}
                        is Resources.Progress -> { }
                        is Resources.Success -> {
                            progressDialog.dismiss()
                            if (data.data!!) {
                                startActivity<RecoveredActivity> { putExtra("fileList", Gson().toJson(videoAdapter.currentList.filter { it.isSelected })) }
                                toast("Video recover successfully")
                                lifecycleScope.launch {
                                    delay(1500)
                                    val list = videoAdapter.currentList.map { it.apply { isSelected = false } }
                                    videoAdapter.submitList(list.toMutableList())
                                    videoAdapter.notifyDataSetChanged()
                                }

                            } else
                                toast("Something went wrong")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvRecoverCounter -> {
                progressDialog.show()
                lifecycleScope.launch {
                    delay(1500)
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
    }

    private fun TickerView.activateOrDeActivate() {
        textColor =
            if (isEnabled) getCompactColor(R.color.activateColor) else getCompactColor(R.color.deActivateColor)
        background =
            getCompactDrawable(if (isEnabled) R.drawable.back_active_stroke else R.drawable.back_inactive_stroke)
    }

}

