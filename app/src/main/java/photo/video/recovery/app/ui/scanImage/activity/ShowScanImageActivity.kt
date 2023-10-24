package photo.video.recovery.app.ui.scanImage.activity

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
import photo.video.recovery.app.R
import photo.video.recovery.app.databinding.ActivityShowScanImageBinding
import photo.video.recovery.app.extension.getCompactColor
import photo.video.recovery.app.extension.getCompactDrawable
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.extension.toast
import photo.video.recovery.app.model.FileModel
import photo.video.recovery.app.ui.recovered.RecoveredActivity
import photo.video.recovery.app.ui.scanImage.ScanImageViewModel
import photo.video.recovery.app.ui.scanImage.adapter.ImageAdapter
import photo.video.recovery.app.utils.Resources
import com.google.gson.Gson
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import photo.video.recovery.app.utils.Constant

class ShowScanImageActivity : AppCompatActivity(), View.OnClickListener {

    private var recoverFileCounter = 0
    private val viewModel by viewModels<ScanImageViewModel>()
    private val imageAdapter by lazy {
        ImageAdapter {
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


    private lateinit var binding: ActivityShowScanImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowScanImageBinding.inflate(layoutInflater)
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
        imageAdapter.submitList(ScanImageViewModel.imageList.toMutableList())
        binding.rvImage.adapter = imageAdapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.recoverImageSharedFlow.collect {data->
                    when (data) {
                        is Resources.Idle -> {}
                        is Resources.Progress ->  {}
                        is Resources.Success -> {
                            progressDialog.dismiss()
                            if (data.data!!) {
                                Constant.recoveryFileList.clear()
                                Constant.recoveryFileList.addAll(imageAdapter.currentList.filter { it.isSelected })
                                startActivity<RecoveredActivity> { }
                                toast("Photo recover successfully")
                                lifecycleScope.launch {
                                  delay(1500)
                                  val list =imageAdapter.currentList.map { it.apply { isSelected = false }}
                                  imageAdapter.submitList(list.toMutableList())
                                  imageAdapter.notifyDataSetChanged()
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
                    viewModel.recoverImageFile(imageAdapter.currentList.filter { it.isSelected })
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
        textColor = if (isEnabled) getCompactColor(R.color.activateColor) else getCompactColor(R.color.deActivateColor)
        background =
            getCompactDrawable(if (isEnabled) R.drawable.back_active_stroke else R.drawable.back_inactive_stroke)
    }

}

