package com.example.recovery.extension

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File


internal fun AppCompatImageView.loadImageFile(file: File,block:Boolean.()->Unit) {
    Glide.with(this)
        .load(file)

        .into(this)

}