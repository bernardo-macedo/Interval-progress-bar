package com.bmacedo.intervalprogressbar.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import com.bmacedo.intervalprogressbar.R

class IntervalSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private var highlightPosition = -1
    private var defaultTickMark: Drawable? = null
    private var highlightedTickMark: Drawable? = null
    private var isDragEnabled = false

    init {
        applyAttributes(attrs, defStyleAttr)
    }

    private fun applyAttributes(rawAttrs: AttributeSet?, defStyleAttr: Int) {
        val attrs =
            context.obtainStyledAttributes(rawAttrs, R.styleable.IntervalSeekBar, defStyleAttr, 0)
        try {
            highlightPosition = attrs.getInt(R.styleable.IntervalSeekBar_highlightPosition, -1)
            defaultTickMark = attrs.getDrawable(R.styleable.IntervalSeekBar_defaultTickMark)
            highlightedTickMark = attrs.getDrawable(R.styleable.IntervalSeekBar_highlightedTickMark)
            isDragEnabled = attrs.getBoolean(R.styleable.IntervalSeekBar_isDragEnabled, false)
        } finally {
            attrs.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isDragEnabled) {
            super.onTouchEvent(event)
        } else {
            true
        }
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }

    private fun drawTickMarks(canvas: Canvas) {
        val defaultTickMark = defaultTickMark ?: return
        val count = max.takeIf { it > 1 } ?: return

        val halfThumbW = thumb.intrinsicWidth / 2
        val availableWidth = width - paddingLeft - paddingRight + thumbOffset * 2 - halfThumbW * 2
        val spacing = availableWidth / count.toFloat()
        val saveCount = canvas.save()

        canvas.translate(paddingLeft - thumbOffset + halfThumbW.toFloat(), height / 2f)
        setTickMarkBounds(defaultTickMark)
        highlightedTickMark?.let { setTickMarkBounds(it) }
        for (i in 0..count) {
            if (i != progress) {
                if (highlightedTickMark != null && i == highlightPosition) {
                    highlightedTickMark?.draw(canvas)
                } else {
                    defaultTickMark.draw(canvas)
                }
            }
            canvas.translate(spacing, 0f)
        }
        canvas.restoreToCount(saveCount)
    }

    private fun setTickMarkBounds(tickMark: Drawable) {
        val w = tickMark.intrinsicWidth
        val h = tickMark.intrinsicHeight
        val halfW = if (w >= 0) w / 2 else 1
        val halfH = if (h >= 0) h / 2 else 1
        tickMark.setBounds(-halfW, -halfH, halfW, halfH)
    }
}