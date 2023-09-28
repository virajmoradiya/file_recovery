package com.example.recovery.ui.scanImage

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recovery.extension.isImageOrGifFile
import com.example.recovery.model.FileModel
import com.example.recovery.utils.Constant
import com.example.recovery.utils.Resources
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


class ScanImageViewModel : ViewModel() {
    companion object {
        var imageList = mutableListOf<FileModel>()
    }

    private val _scanPathMutableStateFlow = MutableStateFlow<String>("")
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
                imageList.addAll(scanImageList)
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

                    val sourceFileChannel: FileChannel? = FileInputStream(fileModel.file).channel
                    val destFileChannel: FileChannel? = FileOutputStream(destFile).channel
                    sourceFileChannel?.transferTo(0, sourceFileChannel.size(), destFileChannel)
                    sourceFileChannel?.close()
                    destFileChannel?.close()

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


