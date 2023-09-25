package com.example.recovery.ui.recovered_media.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recovery.databinding.ItemPhotoBinding
import com.example.recovery.extension.addBounceAnim
import com.example.recovery.extension.gone
import com.example.recovery.extension.invisible
import com.example.recovery.extension.loadImageFile
import com.example.recovery.extension.visible
import com.example.recovery.model.FileModel
import com.github.hariprasanths.bounceview.BounceView

class RecoverImageAdapter(private val listener:View.OnClickListener?=null) : ListAdapter<FileModel, RecoverImageAdapter.Holder>(ImageDiffUtils()) {

    inner class Holder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            val item = currentList[holder.adapterPosition]
            Log.i("acac", "onBindViewHolder: "+item.file.name)
            ivImage.loadImageFile(item.file)
            ivSelect.gone()
            ivUnSelect.gone()
            root.addBounceAnim()
            root.setOnClickListener {
                listener?.onClick(it.apply { tag = currentList[holder.adapterPosition] })
            }
        }
    }
}

class ImageDiffUtils : DiffUtil.ItemCallback<FileModel>() {
    override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem.file.absolutePath.equals(newItem.file.absolutePath)
    }
}
