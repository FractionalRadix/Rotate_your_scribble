package com.cormontia.android.rotateyourscribble

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
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

    /**
     * If the View is rendered for the first time, a few calculations must be done.
     * We use this flag to check if these calculations need to be done, or have been done already.
     */
    private var firstTime = true

    /**
     * Cache the calculated data about the view.
     */
    private lateinit var viewInfo: ViewInfo

    private val blackLinePaint = Paint()

    init {
        blackLinePaint.style = Paint.Style.STROKE
        blackLinePaint.color = Color.BLACK
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
    private fun sendPointsToContext(points: MutableList<PointF>, center: PointF) {
        if (context is MainActivity) {
            val activity = context as MainActivity
            activity.accept(points)
            activity.setCenter(center) //TODO!~ Move this to a caller of its own, to make this call only once.
        }
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        if (firstTime) {
            viewInfo = ViewInfo(width, height, paddingLeft, paddingTop, paddingRight, paddingBottom)
            firstTime = false

            //TODO?~ Move?
            if (context is MainActivity) {
                val activity = context as MainActivity
                activity.setCenter(viewInfo.center)
            }
        }


        canvas.drawRect(viewInfo.rect, blackLinePaint)

        if (localPointsStore.any()) {
            var prevPoint = localPointsStore[0]
            for (pointIdx in 1 until localPointsStore.size) {
                val curPoint = localPointsStore[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackLinePaint)
                prevPoint = curPoint
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(evt: MotionEvent): Boolean {
        // For the moment, we simply assume a single pointer.
        // In later versions, we can work with multiple pointers... and with Android's peculiar way of indexing them.

        val points = mutableListOf<PointF>()
        for (idx in 0 until evt.historySize) {
            points.add(PointF(evt.getHistoricalX(idx), evt.getHistoricalY(idx)))
        }
        points.add(PointF(evt.x, evt.y))
        sendPointsToContext(points, viewInfo.center)

        return true
    }

    //TODO?~ Use an orientation change listener instead of the (apparently more generic) configuration change listener?
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        firstTime = true
    }

    fun setPoints(points: MutableList<PointF>) {
        localPointsStore.clear()
        localPointsStore.addAll(points)
        invalidate()
    }
}