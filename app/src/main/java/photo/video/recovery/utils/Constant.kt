package photo.video.recovery.utils

import photo.video.recovery.model.FileModel

object Constant {
    const val REQUEST_CODE_MANAGE_STORAGE_PERMISSION: Int = 1008
    const val IMAGE_FOLDER = "Image"
    const val VIDEO_FOLDER = "Video"
    const val APP_NAME = "All Recovery"


    val recoveryFileList = mutableListOf<FileModel>()

    //for app data
    var isFlexibleUpdate = true
    var isAppHaveUpdate = true

}