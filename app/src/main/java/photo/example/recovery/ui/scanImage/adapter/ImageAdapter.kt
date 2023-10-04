package photo.video.recovery.ui.scanImage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import photo.video.recovery.databinding.ItemPhotoBinding
import photo.video.recovery.extension.gone
import photo.video.recovery.extension.invisible
import photo.video.recovery.extension.loadImageFile
import photo.video.recovery.extension.visible
import photo.video.recovery.model.FileModel

class ImageAdapter(private val listener:View.OnClickListener?=null) : ListAdapter<FileModel, ImageAdapter.Holder>(ImageDiffUtils()) {

    inner class Holder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            val item = currentList[holder.adapterPosition]
            ivImage.loadImageFile(item.file){
                ivDefaultImage.gone()
            }
            if (item.isSelected) {
                ivSelect.visible()
                ivUnSelect.invisible()
            } else {
                ivSelect.invisible()
                ivUnSelect.visible()
            }
            root.setOnClickListener {
                currentList[holder.adapterPosition].isSelected = !item.isSelected
                notifyItemChanged(holder.adapterPosition)
                listener?.onClick(it.apply { tag = currentList[holder.adapterPosition] })
            }
        }
    }
}

class ImageDiffUtils : DiffUtil.ItemCallback<FileModel>() {
    override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem.file.absolutePath.equals(newItem.file.absolutePath) && oldItem.isSelected==newItem.isSelected
    }
}

