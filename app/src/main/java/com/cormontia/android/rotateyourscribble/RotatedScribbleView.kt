package com.cormontia.android.rotateyourscribble

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.GestureDetectorCompat
import com.cormontia.android.rotateyourscribble.matrix3d.MatrixFactory
import com.cormontia.android.rotateyourscribble.matrix3d.Vec4
import kotlin.math.abs

/**
 * Show the 3D wireframe that you get, when you rotate the scribble around the Y-axis.
 */
class RotatedScribbleView : View {

    //private var basePoints = mutableListOf<PointF>()
    private var frame3D = listOf<List<Vec4>>()
    private var rotatedLines = listOf<List<PointF>>()

    //TODO?~ These aren't always initialized in time....
    //Note that these values are the center of the SCRIBBLE, not the center of the View!
    // (In other words, these are world coordinates, rather than view coordinates).
    private var center = PointF(0.0f,0.0f)    // Placeholder value. TODO?~ Use lateinit?

    fun setCenter(center: PointF) {
        this.center = center
    }

    /**
     * If the View is rendered for the first time, a few calculations must be done.
     * We use this flag to check if these calculations need to be done, or have been done already.
     */
    private var firstTime = true

    /**
     * Cache the calculated data about the view.
     */
    private var viewInfo: ViewInfo? = null

    private val blackLinePaint = Paint()
    init {
        blackLinePaint.color = Color.BLACK
        blackLinePaint.style = Paint.Style.STROKE
    }

    private lateinit var mGestureDetector: GestureDetectorCompat

    private val rightwardAnimator = ValueAnimator.ofFloat(0.0f, (2.0f * Math.PI).toFloat())
    private val leftwardAnimator = ValueAnimator.ofFloat((2.0f * Math.PI).toFloat(), 0.0f)
    private var yRotation = 0.0f
    private val upwardAnimator = ValueAnimator.ofFloat(0.0f, (2.0f * Math.PI).toFloat())
    private val downwardAnimator = ValueAnimator.ofFloat((2.0f * Math.PI).toFloat(), 0.0f)
    private var xRotation = 0.0f

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

        mGestureDetector = GestureDetectorCompat(context, FlingGestureListener())

        leftwardAnimator.addUpdateListener {
            yRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate3DModel(center, frame3D, 0.0, yRotation.toDouble())
            invalidate()
        }
        leftwardAnimator.interpolator = DecelerateInterpolator()
        leftwardAnimator.duration = 2000

        rightwardAnimator.addUpdateListener {
            yRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate3DModel(center, frame3D,0.0, yRotation.toDouble())
            invalidate()
        }
        rightwardAnimator.interpolator = DecelerateInterpolator()
        rightwardAnimator.duration = 2000

        upwardAnimator.addUpdateListener {
            xRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate3DModel(center, frame3D, xRotation.toDouble(), 0.0)
            invalidate()
        }
        upwardAnimator.interpolator = DecelerateInterpolator()
        upwardAnimator.duration = 2000

        downwardAnimator.addUpdateListener {
            xRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate3DModel(center, frame3D, xRotation.toDouble(), 0.0)
            invalidate()
        }
        downwardAnimator.interpolator = DecelerateInterpolator()
        downwardAnimator.duration = 2000

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (firstTime) {
            viewInfo = ViewInfo(width, height, paddingLeft, paddingTop, paddingRight, paddingBottom)
            firstTime = false
        }

        canvas.drawRect(viewInfo!!.rect, blackLinePaint)

        for (line in rotatedLines) {
            drawPointList(canvas, line)
        }
    }

    private fun drawPointList(canvas: Canvas, pointList: List<PointF>) {
        if (pointList.any()) {
            var prevPoint = pointList[0]
            for (pointIdx in 1 until pointList.size) {
                val curPoint = pointList[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackLinePaint)
                prevPoint = curPoint
            }
        }
    }

    fun set3DModel(model: List<List<Vec4>>) {
        frame3D = model
        rotatedLines = rotate3DModel(center, frame3D, 0.0, 0.0)
        invalidate()
    }

    /**
     * Rotate a 3D model around the x-axis and the y-axis.
     */
    private fun rotate3DModel(center: PointF, lines: List<List<Vec4>>, xRotation: Double, yRotation: Double): List<List<PointF>> {
        val translateBefore = MatrixFactory.Translate(-center.x.toDouble(), -center.y.toDouble(), 0.0)
        val translateAfter = MatrixFactory.Translate(+center.x.toDouble(), +center.y.toDouble(), 0.0)
        val animationRotationX = MatrixFactory.RotateAroundX(xRotation)
        val animationRotationY = MatrixFactory.RotateAroundY(yRotation)
        val fullMatrix = translateAfter * animationRotationX * animationRotationY * translateBefore

        val rotatedLines = mutableListOf<MutableList<PointF>>()
        for (line in lines) {
            val rotatedLine = mutableListOf<PointF>()
            line.listIterator().forEach {
                val rotatedPointAsVector = fullMatrix.multiply(it)
                val rotatedPoint = PointF(rotatedPointAsVector[0].toFloat(), rotatedPointAsVector[1].toFloat())
                rotatedLine.add(rotatedPoint)
            }
            rotatedLines.add(rotatedLine)
        }
        return rotatedLines
    }

    // Gesture detection

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) { //TODO?- The situation that event==null shouldn't occur.
            mGestureDetector.onTouchEvent(event)
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    inner class FlingGestureListener: GestureDetector.SimpleOnGestureListener() {
        // Should almost always be overridden to return `true`.
        // "If you return false from onDown(), as GestureDetector.SimpleOnGestureListener does by default,
        //  the system assumes that you want to ignore the rest of the gesture, and the other methods of GestureDetector.OnGestureListener never get called."
        // (Source: https://developer.android.com/training/gestures/detector#detect-a-subset-of-supported-gestures )
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float,
        ): Boolean {
            val rightward = e2.x - e1.x
            val upward = e2.y - e1.y
            if (abs(rightward) > abs(upward)) {
                if (rightward > 0) {
                    rightwardAnimator.start()
                } else {
                    leftwardAnimator.start()
                }
            } else {
                if (upward > 0) {
                    upwardAnimator.start()
                } else {
                    downwardAnimator.start()
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}