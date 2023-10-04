package photo.video.recovery.ui.recovered_media.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import photo.video.recovery.databinding.ActivityRecoveredFileBinding
import photo.video.recovery.ui.recovered_media.fragment.PhotoFragment
import photo.video.recovery.ui.recovered_media.fragment.VideoFragment

class RecoveredFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoveredFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoveredFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initViewPager()
    }

    private fun initToolbar() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initViewPager() {
        binding.apply {
            val list = arrayListOf<Fragment>()
            list.add(PhotoFragment())
            list.add(VideoFragment())
            pager.adapter = PagerAdapter(list)
            tabLayout.setViewPager2( pager,this@RecoveredFileActivity,list,listOf("Image", "Video"))
            tabLayout.setViewPage2ScrollListener({
                pager.currentItem = it
            })
        }
    }

    inner class PagerAdapter(
        private val fragmentList: List<Fragment>
    ) : FragmentStateAdapter(this) {
        override fun getItemCount() = fragmentList.size

        override fun createFragment(position: Int) = fragmentList[position]


    }
}