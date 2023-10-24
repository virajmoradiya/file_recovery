package photo.video.recovery.app.ui.recovered_media

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import photo.video.recovery.app.extension.isImageOrGifFile
import photo.video.recovery.app.extension.isVideoFile
import photo.video.recovery.app.model.FileModel
import photo.video.recovery.app.utils.Constant
import photo.video.recovery.app.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class MediaViewModel : ViewModel() {

    private val _imageMutableStateFlow =
        MutableStateFlow<Resources<MutableList<FileModel>>>(Resources.Idle(""))
    val imageMutableStateFlow = _imageMutableStateFlow.asSharedFlow()

    private val _videoMutableStateFlow =
        MutableStateFlow<Resources<MutableList<FileModel>>>(Resources.Idle(""))
    val videoImageSharedFlow = _videoMutableStateFlow.asSharedFlow()

    init {
        getMediaData()
    }

    private fun getMediaData() {
        viewModelScope.launch(Dispatchers.IO) {
            val photoFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),Constant.APP_NAME + "/" + Constant.IMAGE_FOLDER)
            val videoFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),Constant.APP_NAME + "/" + Constant.VIDEO_FOLDER)

            val imageList =
                photoFolder.listFiles()?.toMutableList()?.filter { it.isImageOrGifFile() }
                    ?.map { FileModel(it) }
            val videoList = videoFolder.listFiles()?.toMutableList()?.filter { it.isVideoFile() }
                ?.map { FileModel(it) }


            _imageMutableStateFlow.tryEmit(Resources.Success( imageList?.sortedByDescending { it.file.lastModified() }?.toMutableList()))
            _videoMutableStateFlow.tryEmit(Resources.Success(videoList?.sortedByDescending { it.file.lastModified() }?.toMutableList()))
        }
    }
}