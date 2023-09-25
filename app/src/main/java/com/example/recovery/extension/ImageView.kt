package com.example.recovery.extension

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.io.File


internal fun AppCompatImageView.loadImageFile(file: File) {
    Glide.with(this)
        .load(file)
        .transform(CenterCrop(),  RoundedCorners(28))
        .into(this)

}