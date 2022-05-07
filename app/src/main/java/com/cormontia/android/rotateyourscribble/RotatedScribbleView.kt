package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class RotatedScribbleView : View {

    private var basePoints = mutableListOf<PointF>()

    private val blackPaint = Paint()
    init {
        blackPaint.color = Color.BLACK
        blackPaint.style = Paint.Style.FILL_AND_STROKE
    }

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
            attrs, R.styleable.RotatedScribbleView, defStyle, 0
        )

        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //TODO!~ Rotate the scribble and draw it...
        // Maybe we should do the rotating in "setPoints"...

        //TODO!~ We shouldn't draw the "basePoints", but the ROTATED points...
        if (basePoints.any()) {
            var prevPoint = basePoints[0]
            for (pointIdx in 1 until basePoints.size) {
                val curPoint = basePoints[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackPaint)
                prevPoint = curPoint
            }
        }
    }

    fun setPoints(points: MutableList<PointF>) {
        basePoints = points
        invalidate()
    }
}