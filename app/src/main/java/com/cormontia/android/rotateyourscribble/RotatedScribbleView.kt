package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class RotatedScribbleView : View {

    private var basePoints = mutableListOf<PointF>()

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

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom


        //TODO!~ Rotate the scribble and draw it...
        // Maybe we should do the rotating in "setPoints"...
        val greenPaint = Paint()
        greenPaint.color = Color.GREEN
        greenPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle(40f, 40f, 30f, greenPaint)

        //TODO!~ We shouldn't draw the "basePoints", but the ROTATED points...
        if (basePoints.any()) {
            var prevPoint = basePoints[0]
            for (pointIdx in 1 until basePoints.size) {
                var curPoint = basePoints[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, greenPaint)
                prevPoint = curPoint
            }
        }
    }

    fun setPoints(points: MutableList<PointF>) {
        basePoints = points
        invalidate()
    }
}