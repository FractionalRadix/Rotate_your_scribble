package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * A drawing area, that allows the user to draw a line (a "scribble").
 * The line is then sent to the ViewModel, so that it can be processed by other parts of the program.
 */
class FlatScribbleView : View {

    /**
     * The list of points that the user has drawn.
     */
    private val localPointsStore = mutableListOf<PointF>()

    var centerX = 0.0 // Placeholder value, since primitive values do not support "lateinit".
    var centerY = 0.0 // Placeholder value, since primitive values do not support "lateinit".

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
            attrs, R.styleable.FlatScribbleView, defStyle, 0
        )

        a.recycle()
    }

    //TODO?~ Do this more neat, by making MainActivity implement an interface that accepts points lists.
    // Then we can simply check if "context" implements that interface, and if so, use it.
    private fun sendPointsToContext(points: MutableList<PointF>, centerX: Double, centerY: Double) {
        if (context is MainActivity) {
            val activity = context as MainActivity
            activity.accept(points)
            activity.setCenter(centerX, centerY) //TODO!~ Move this to a caller of its own, to make this call only once.
        }
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        //TODO! Calculate these values only once and cache them.

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val contentWidth = width - paddingLeft - paddingRight
        centerX = (contentWidth / 2).toDouble()

        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val contentHeight = height - paddingTop - paddingBottom
        centerY = (contentHeight / 2).toDouble()

        if (localPointsStore.any()) {
            var prevPoint = localPointsStore[0]
            for (pointIdx in 1 until localPointsStore.size) {
                val curPoint = localPointsStore[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackPaint)
                prevPoint = curPoint
            }
        }
    }

    override fun onTouchEvent(evt: MotionEvent): Boolean {
        // For the moment, we simply assume a single pointer.
        // In later versions, we can work with multiple pointers... and with Android's peculiar way of indexing them.

        val points = mutableListOf<PointF>()
        for (idx in 0 until evt.historySize) {
            points.add(PointF(evt.getHistoricalX(idx), evt.getHistoricalY(idx)))
        }
        points.add(PointF(evt.x, evt.y))
        sendPointsToContext(points, centerX, centerY)

        return true
    }

    fun setPoints(points: MutableList<PointF>) {
        localPointsStore.clear()
        localPointsStore.addAll(points)
        invalidate()
    }
}