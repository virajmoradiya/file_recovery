package com.example.recovery.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recovery.R
import com.example.recovery.databinding.ActivityDashboardBinding
import com.example.recovery.extension.changeStatusBarColor
import com.github.hariprasanths.bounceview.BounceView

class DashboardActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intiView()
    }

    private fun intiView() {
        BounceView.addAnimTo(binding.tvVideo)
        BounceView.addAnimTo(binding.tvRecoveredFile)
        BounceView.addAnimTo(binding.tvSetting)
        BounceView.addAnimTo(binding.tvImage)

    }
}