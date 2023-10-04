package photo.video.recovery.ui.scanVideo

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import photo.video.recovery.extension.isVideoFile
import photo.video.recovery.model.FileModel
import photo.video.recovery.utils.Constant
import photo.video.recovery.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.Stack


class ScanVideoViewModel : ViewModel() {
    companion object {
        var videoList = mutableListOf<FileModel>()
    }

    private val _scanPathMutableStateFlow = MutableStateFlow("")
    val scanPathSharedFlow = _scanPathMutableStateFlow.asSharedFlow()

    private val _scanVideoMutableStateFlow =
        MutableStateFlow<Resources<MutableList<FileModel>>>(Resources.Idle(""))
    val scanVideoSharedFlow = _scanVideoMutableStateFlow.asSharedFlow()

    private val _recoverVideoMutableStateFlow =
        MutableStateFlow<Resources<Boolean>>(Resources.Idle("", false))
    val recoverVideoSharedFlow = _recoverVideoMutableStateFlow.asSharedFlow()

    var isRecoverProgressOn = false


    fun startScanning() {
        viewModelScope.launch(Dispatchers.IO) {

            val scanVideoList = mutableListOf<FileModel>()

            val directoryList =
                Environment.getExternalStorageDirectory().listFiles()?.toMutableList()
                    ?: mutableListOf()

            val stack = Stack<String>()
            for (file in directoryList) {
                stack.push(file.absolutePath)
            }
            while (stack.isNotEmpty()) {
                val folderPath = stack.pop()
                val folder = File(folderPath)
                delay(20)
                if (!folder.listFiles().isNullOrEmpty()) {
                    for (file in folder.listFiles()!!) {
                        _scanPathMutableStateFlow.tryEmit(file.absolutePath)
                        if (file.isDirectory) {
                            stack.push(file.absolutePath)
                        } else if (file.isVideoFile()) {
                            scanVideoList.add(FileModel(file))
                        }
                    }
                    _scanVideoMutableStateFlow.tryEmit(Resources.Progress(scanVideoList.size))
                }
            }
            if (stack.isEmpty()) {
                delay(500)
                videoList.clear()
                videoList.addAll(scanVideoList)
                delay(500)
                if (videoList.isEmpty())
                    _scanVideoMutableStateFlow.tryEmit(Resources.Error("", videoList))
                else
                    _scanVideoMutableStateFlow.tryEmit(Resources.Success(videoList))
            }
        }
    }

    fun recoverVideoFile(list: List<FileModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            isRecoverProgressOn = true
            _recoverVideoMutableStateFlow.tryEmit(Resources.Progress(0))
            delay(1000)
            for (fileModel in list) {
                try {
                    val folder = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constant.APP_NAME + "/" + Constant.VIDEO_FOLDER
                    )
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    val destFile = File(folder.absolutePath + "/" + fileModel.file.name)

                    val sourceFileChannel: FileChannel? = FileInputStream(fileModel.file).channel
                    val destFileChannel: FileChannel? = FileOutputStream(destFile).channel
                    sourceFileChannel?.transferTo(0, sourceFileChannel.size(), destFileChannel)
                    sourceFileChannel?.close()
                    destFileChannel?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _recoverVideoMutableStateFlow.tryEmit(Resources.Success(false))
                }
            }
            delay(1000)
            _recoverVideoMutableStateFlow.tryEmit(Resources.Success(true))
            isRecoverProgressOn = false
        }
    }
}


