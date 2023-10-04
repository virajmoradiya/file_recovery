package photo.video.recovery.ui.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import photo.video.recovery.R
import photo.video.recovery.extension.getCompactColor
import photo.video.recovery.extension.invisible
import photo.video.recovery.extension.visible

class RippleBackground : RelativeLayout {
    private var rippleColor = 0
    private var rippleStrokeWidth = 0f
    private var rippleRadius = 0f
    private var rippleDurationTime = 0
    private var rippleAmount = 0
    private var rippleDelay = 0
    private var rippleScale = 0f
    private var rippleType = 0
    private var paint: Paint? = null
    private var isRippleAnimationRunning = false
        private set
    private var animatorSet: AnimatorSet? = null
    private var animatorList: ArrayList<Animator>? = null
    private var rippleParams: LayoutParams? = null
    private val rippleViewList = ArrayList<RippleView>()

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return
        requireNotNull(attrs) { "Attributes should be provided to this view," }
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RippleBackground)
        rippleColor = typedArray.getColor(
            R.styleable.RippleBackground_rb_color,
            getCompactColor(R.color.waveAnimationColor)
        )
        rippleStrokeWidth = typedArray.getDimension(
            R.styleable.RippleBackground_rb_strokeWidth,
            resources.getDimension(R.dimen.rippleStrokeWidth)
        )
        rippleRadius = typedArray.getDimension(
            R.styleable.RippleBackground_rb_radius,
            resources.getDimension(R.dimen.rippleRadius)
        )
        rippleDurationTime =
            typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount =
            typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleBackground_rb_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()
        rippleDelay = rippleDurationTime / rippleAmount
        paint = Paint()
        paint?.isAntiAlias = true
        if (rippleType == DEFAULT_FILL_TYPE) {
            rippleStrokeWidth = 0f
            paint?.style = Paint.Style.FILL
        } else paint?.style = Paint.Style.STROKE
        paint?.color = rippleColor
        rippleParams = LayoutParams(
            (1.2 * (rippleRadius + rippleStrokeWidth)).toInt(),
            (1.2 * (rippleRadius + rippleStrokeWidth)).toInt()
        )
        rippleParams?.addRule(CENTER_IN_PARENT, TRUE)
        animatorSet = AnimatorSet()
        animatorSet?.interpolator = AccelerateDecelerateInterpolator()
        animatorList = ArrayList()
        for (i in 0 until rippleAmount) {
            val rippleView = RippleView(getContext())
            addView(rippleView, rippleParams)
            rippleViewList.add(rippleView)
            val scaleXAnimator: ObjectAnimator =
                ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            scaleXAnimator.duration = rippleDurationTime.toLong()
            animatorList?.add(scaleXAnimator)
            val scaleYAnimator: ObjectAnimator =
                ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            scaleYAnimator.duration = rippleDurationTime.toLong()
            animatorList?.add(scaleYAnimator)
            val alphaAnimator: ObjectAnimator =
                ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            alphaAnimator.duration = rippleDurationTime.toLong()
            animatorList?.add(alphaAnimator)
        }
        animatorSet?.playTogether(animatorList)
    }

    private inner class RippleView(context: Context?) : View(context) {
        init {
            this.invisible()
        }

        override fun onDraw(canvas: Canvas) {
            val radius: Int = width.coerceAtMost(height) / 2
            paint?.let {
                canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius - rippleStrokeWidth,it)
            }
        }
    }

    fun startRippleAnimation() {
        if (!isRippleAnimationRunning) {
            for (rippleView in rippleViewList) {
                rippleView.visible()
            }
            animatorSet?.start()
            isRippleAnimationRunning = true
        }
    }



    companion object {
        private const val DEFAULT_RIPPLE_COUNT = 2
        private const val DEFAULT_DURATION_TIME = 4000
        private const val DEFAULT_SCALE = 2.3f
        private const val DEFAULT_FILL_TYPE = 0
    }
}