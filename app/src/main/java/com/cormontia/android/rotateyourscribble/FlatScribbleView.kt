package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * TODO: document your custom view class.
 */
class FlatScribbleView : View {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.FlatScribbleView, defStyle, 0
        )

        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val redPaint = Paint()
        redPaint.color = Color.RED
        redPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle(40f, 40f, 30f, redPaint)
    }

    override fun onTouchEvent(evt: MotionEvent): Boolean {
        //TODO!+
        return true
    }
}