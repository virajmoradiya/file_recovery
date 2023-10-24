package photo.video.recovery.app.ui.recovered_media.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import photo.video.recovery.app.R
import photo.video.recovery.app.databinding.FragmentVideoBinding
import photo.video.recovery.app.delegate.viewBinding
import photo.video.recovery.app.extension.gone
import photo.video.recovery.app.extension.startActivity
import photo.video.recovery.app.extension.visible
import photo.video.recovery.app.extension.visibleIf
import photo.video.recovery.app.model.FileModel
import photo.video.recovery.app.ui.recovered_media.MediaViewModel
import photo.video.recovery.app.ui.recovered_media.activity.ShowMediaActivity
import photo.video.recovery.app.ui.recovered_media.adapter.RecoverVideoAdapter
import photo.video.recovery.app.utils.Resources
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

                        else -> {}
                    }
                }
            }
        }
    }
}