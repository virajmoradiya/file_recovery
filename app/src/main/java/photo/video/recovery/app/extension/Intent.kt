package photo.video.recovery.app.extension

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

internal inline fun <reified T : AppCompatActivity> Context.startActivity(block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    intent.block()
    startActivity(intent)
}

internal inline fun <reified T : AppCompatActivity> AppCompatActivity.startActivityForResult(requestCode: Int, block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    intent.block()
    startActivityForResult(intent, requestCode)
}

internal inline fun <reified T : AppCompatActivity> Fragment.startActivity(block: Intent.() -> Unit) {
    val intent = Intent(requireContext(), T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    intent.block()
    startActivity(intent)
}

internal inline fun <reified T : AppCompatActivity> Fragment.startActivityForResult(requestCode: Int, block: Intent.() -> Unit) {
    val intent = Intent(requireContext(), T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    intent.block()
    startActivityForResult(intent, requestCode)
}