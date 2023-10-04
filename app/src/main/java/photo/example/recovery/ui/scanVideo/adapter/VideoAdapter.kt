package photo.video.recovery.ui.scanVideo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import photo.video.recovery.databinding.ItemVideoBinding
import photo.video.recovery.extension.gone
import photo.video.recovery.extension.invisible
import photo.video.recovery.extension.loadImageFile
import photo.video.recovery.extension.visible
import photo.video.recovery.model.FileModel

class VideoAdapter(private val listener:View.OnClickListener?=null) : ListAdapter<FileModel, VideoAdapter.Holder>(
    VideoDiffUtils()
) {

    inner class Holder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            val item = currentList[holder.adapterPosition]
            ivVideo.loadImageFile(item.file){
                ivDefaultVideo.gone()
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

class VideoDiffUtils : DiffUtil.ItemCallback<FileModel>() {
    override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem== newItem
    }

    override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem.file.absolutePath.equals(newItem.file.absolutePath)
    }
}

