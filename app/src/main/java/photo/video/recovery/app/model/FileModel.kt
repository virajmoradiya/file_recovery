package photo.video.recovery.app.model

import java.io.File

data class FileModel(val file: File, var isSelected:Boolean=false,var image1:File?=null,var image2:File?=null,var image3:File?=null)
