package photo.video.recovery.ui.recovered_media.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import photo.video.recovery.databinding.ItemVideoBinding
import photo.video.recovery.extension.addBounceAnim
import photo.video.recovery.extension.gone
import photo.video.recovery.extension.invisible
import photo.video.recovery.extension.loadImageFile
import photo.video.recovery.extension.visible
import photo.video.recovery.model.FileModel
import com.github.hariprasanths.bounceview.BounceView

class RecoverVideoAdapter(private val listener:View.OnClickListener?=null) : ListAdapter<FileModel, RecoverVideoAdapter.Holder>(
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
            ivSelect.gone()
            ivUnSelect.gone()

            root.addBounceAnim()
            root.setOnClickListener {
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

