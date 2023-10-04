package photo.video.recovery.extension

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File

internal fun Context.getCompactColor(color: Int):Int= ContextCompat.getColor(this,color)

internal fun View.getCompactColor(color: Int):Int= ContextCompat.getColor(this.context,color)

internal fun Context.getCompactDrawable(drawable: Int):Drawable?= ContextCompat.getDrawable(this,drawable)

internal fun View.getCompactDrawable(drawable: Int):Drawable?= ContextCompat.getDrawable(context,drawable)

internal fun AppCompatActivity.changeStatusBarColor(color: Int) {
    window.statusBarColor = getCompactColor(color)
}

internal fun File.isImageOrGifFile(): Boolean  = kotlin.run { name.endsWith("png")||name.endsWith("jpg")||name.endsWith("jpeg")||name.endsWith("gif") }

internal fun File.isVideoFile(): Boolean  = kotlin.run { name.endsWith("mp4")||name.endsWith("mkv")||name.endsWith("mov") }

internal fun Context.hasPermissions(permissionList: MutableList<String>): Boolean {
    var hasAllPermissions = true
    for (permission in permissionList) {
        if (checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false
        }
    }
    return hasAllPermissions
}