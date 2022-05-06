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

    //TODO!- Stop using this, we are sending the points to the ViewModel instead.
    private val localPointsStore = mutableListOf<PointF>()

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
    private fun sendPointsToContext(points: MutableList<PointF>) {
        if (context is MainActivity) {
            val activity = context as MainActivity
            Log.i("FlatScribbleView", "Sending points to MainActivity: $points")
            activity.accept(points)
        }
    }

    fun setPoints(points: MutableList<PointF>) {
        //TODO!+ Draw the points...
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

        val blackPaint = Paint()
        blackPaint.color = Color.BLACK
        blackPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle(40f, 40f, 30f, blackPaint)

        //TODO!~ Use the point list that you get from the ViewModel.
        if (localPointsStore.any()) {
            var prevPoint = localPointsStore[0]
            for (pointIdx in 1 until localPointsStore.size) {
                var curPoint = localPointsStore[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackPaint)
                prevPoint = curPoint
            }
        }
    }

    override fun onTouchEvent(evt: MotionEvent): Boolean {
        // For the moment, we simply assume a single pointer.
        // In later versions, we can work with multiple pointers... and with Android's weird way of indexing them.

        var points = mutableListOf<PointF>()
        for (idx in 0 until evt.historySize) {
            points.add(PointF(evt.getHistoricalX(idx), evt.getHistoricalY(idx)))
            localPointsStore.add(PointF(evt.getHistoricalX(idx), evt.getHistoricalY(idx))) //TODO!-
        }
        points.add(PointF(evt.x, evt.y))
        localPointsStore.add(PointF(evt.x, evt.y)) //TODO!-
        Log.i("FlatScribbleView", "Sending points to view owner: $points")
        sendPointsToContext(points)

        //TODO?~ I think this should go via the ViewModel
        invalidate()

        return true
    }
}