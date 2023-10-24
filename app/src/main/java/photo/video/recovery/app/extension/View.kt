package photo.video.recovery.app.extension

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.hariprasanths.bounceview.BounceView

internal fun View.visibleIf(condition: Boolean) = kotlin.run { if (condition) visible() else gone() }

internal fun View.visible() = kotlin.run { this.visibility = View.VISIBLE }

internal fun View.invisible() = kotlin.run { this.visibility = View.INVISIBLE }

internal fun View.gone() = kotlin.run { this.visibility = View.GONE }

internal fun Context.toast(msg: String) = kotlin.run { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }


internal fun View.addBounceAnim() {
    BounceView.addAnimTo(this).setScaleForPushInAnim(0.95F,0.95F)
        .setScaleForPopOutAnim(1f,1f)
}