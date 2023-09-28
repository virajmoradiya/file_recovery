package com.example.recovery.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.recovery.BuildConfig
import com.example.recovery.R
import com.example.recovery.databinding.ActivitySettingBinding
import com.example.recovery.extension.addBounceAnim


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.tvVersion.text = "Version " + BuildConfig.VERSION_NAME

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvSuggestion.setOnClickListener(this)
        binding.tvShare.setOnClickListener(this)
        binding.tvPrivacy.setOnClickListener(this)

        binding.tvSuggestion.addBounceAnim()
        binding.tvShare.addBounceAnim()
        binding.tvPrivacy .addBounceAnim()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSuggestion -> composeEmail()
            R.id.tvShare -> shareApp()
            R.id.tvPrivacy -> openWebUrl(getString(R.string.url_privacy_policy))
        }
    }

    private fun openWebUrl(url: String) {
        val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(sendIntent)
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """ ${shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID}""".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun composeEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:"+getString(R.string.email))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}