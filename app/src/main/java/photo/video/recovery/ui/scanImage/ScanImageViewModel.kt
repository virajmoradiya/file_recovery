package photo.video.recovery.ui.scanImage

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import photo.video.recovery.extension.isImageOrGifFile
import photo.video.recovery.model.FileModel
import photo.video.recovery.utils.Constant
import photo.video.recovery.utils.Resources
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Stack


class ScanImageViewModel : ViewModel() {
    companion object {
        var imageList = mutableListOf<FileModel>()
    }

    private val _scanPathMutableStateFlow = MutableStateFlow("")
    val scanPathSharedFlow = _scanPathMutableStateFlow.asSharedFlow()

    private val _scanImageMutableStateFlow =
        MutableStateFlow<Resources<MutableList<FileModel>>>(Resources.Idle(""))
    val scanImageSharedFlow = _scanImageMutableStateFlow.asSharedFlow()

    private val _recoverImageMutableStateFlow =
        MutableStateFlow<Resources<Boolean>>(Resources.Idle("", false))
    val recoverImageSharedFlow = _recoverImageMutableStateFlow.asSharedFlow()

    var isRecoverProgressOn = false


    fun startScanning() {
        viewModelScope.launch(Dispatchers.IO) {

            val scanImageList = mutableListOf<FileModel>()

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
                        } else if (file.isImageOrGifFile()) {
                            scanImageList.add(FileModel(file))
                        }
                    }
                    _scanImageMutableStateFlow.tryEmit(Resources.Progress(scanImageList.size))
                }
            }
            if (stack.isEmpty()) {
                delay(500)
                imageList.clear()
                imageList.addAll( scanImageList.sortedByDescending { it.file.lastModified() })
                delay(500)
                if (imageList.isEmpty())
                    _scanImageMutableStateFlow.tryEmit(Resources.Error("", imageList))
                else
                    _scanImageMutableStateFlow.tryEmit(Resources.Success(imageList))
            }
        }
    }

    fun recoverImageFile(list: List<FileModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            isRecoverProgressOn = true
            _recoverImageMutableStateFlow.tryEmit(Resources.Progress(0))
            delay(1000)
            for (fileModel in list) {
                try {
                   val folder = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constant.APP_NAME + "/" + Constant.IMAGE_FOLDER
                    )
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }

                      val destFile = File(folder.absolutePath + "/" + fileModel.file.name)

                    var inputStream: InputStream?
                    var out: OutputStream?
                    try {
                        inputStream = FileInputStream(fileModel.file.absolutePath)
                        out = FileOutputStream(destFile.absolutePath)
                        val buffer = ByteArray(1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            out.write(buffer, 0, read)
                        }
                        inputStream.close()
                        out.flush()
                        out.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    _recoverImageMutableStateFlow.tryEmit(Resources.Success(false))
                }
            }
            delay(1000)
            _recoverImageMutableStateFlow.tryEmit(Resources.Success(true))
            isRecoverProgressOn = false
        }
    }
}


