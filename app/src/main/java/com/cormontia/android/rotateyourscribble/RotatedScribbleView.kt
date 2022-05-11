package com.cormontia.android.rotateyourscribble

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
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

    private var basePoints = mutableListOf<PointF>()
    private var rotatedLines = listOf<List<PointF>>()

    private var centerY = 0.0 // Placeholder value, since lateinit is not allowed on primitive types.
    private var centerX = 0.0 // Placeholder value, since lateinit is not allowed on primitive types.

    private val blackPaint = Paint()
    init {
        blackPaint.color = Color.BLACK
        blackPaint.style = Paint.Style.FILL_AND_STROKE
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
            rotatedLines = rotate(basePoints)
            invalidate()
        }
        leftwardAnimator.interpolator = DecelerateInterpolator()
        leftwardAnimator.duration = 2000

        rightwardAnimator.addUpdateListener {
            yRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate(basePoints)
            invalidate()
        }
        rightwardAnimator.interpolator = DecelerateInterpolator()
        rightwardAnimator.duration = 2000

        upwardAnimator.addUpdateListener {
            xRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate(basePoints)
            invalidate()
        }
        upwardAnimator.interpolator = DecelerateInterpolator()
        upwardAnimator.duration = 2000

        downwardAnimator.addUpdateListener {
            xRotation = it.animatedValue as Float //TODO?~ Use it.animatedFraction instead?
            rotatedLines = rotate(basePoints)
            invalidate()
        }
        downwardAnimator.interpolator = DecelerateInterpolator()
        downwardAnimator.duration = 2000

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.i("RotatedScribbleView", "In onDraw(Canvas) method.")

        for (line in rotatedLines) {
            drawPointList(canvas, line)
        }
    }

    private fun drawPointList(canvas: Canvas, pointList: List<PointF>) {
        if (pointList.any()) {
            var prevPoint = pointList[0]
            for (pointIdx in 1 until pointList.size) {
                val curPoint = pointList[pointIdx]
                canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, blackPaint)
                prevPoint = curPoint
            }
        }
    }

    fun setPoints(points: MutableList<PointF>) {
        basePoints = points
        rotatedLines = rotate(basePoints)
        invalidate()
    }

    fun setCenter(centerX: Double, centerY: Double) {
        this.centerX = centerX
        this.centerY = centerY
    }

    private fun rotate(points: MutableList<PointF>) : List<List<PointF>> {
        val rotatedLines = mutableListOf<MutableList<PointF>>()

        val translateBefore = MatrixFactory.Translate(-centerX, -centerY, 0.0)
        val translateAfter = MatrixFactory.Translate(+centerX, +centerY, 0.0)
        for (angleInDegrees in 0 until 360 step 5) {
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
            val rotation = MatrixFactory.RotateAroundY(angleInRadians)

            val animationRotationX = MatrixFactory.RotateAroundX(xRotation.toDouble())
            val animationRotationY = MatrixFactory.RotateAroundY(yRotation.toDouble())

            val fullMatrix = translateAfter * animationRotationX * animationRotationY * rotation * translateBefore

            val rotatedLine = mutableListOf<PointF>()
            for (point in points) {
                val pointAsVector = Vec4(point.x.toDouble(), point.y.toDouble(), 0.0, 1.0)
                val rotatedPointAsVector = fullMatrix.multiply(pointAsVector)
                val rotatedPoint = PointF(rotatedPointAsVector[0].toFloat(), rotatedPointAsVector[1].toFloat())
                rotatedLine.add(rotatedPoint)
            }

            rotatedLines.add(rotatedLine)
        }
        return rotatedLines
    }

    // Gesture detection

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i("RotatedScribbleView", "Entered onTouchEvent")
        mGestureDetector.onTouchEvent(event)
        if (event != null) {
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
            Log.i("RotatedScribbleView.FlingGestureListener", "Entered onDown(...)")
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.i("RotatedScribbleView.FlingGestureListener", "Entered onFling(...)")
            if (e1 != null && e2 != null) {
                val rightward = e2.x - e1.x
                val upward = e2.y - e1.y
                if (abs(rightward) > abs(upward)) {
                    if (rightward > 0) {
                        Log.i("RotatedScribbleView", "User flinged to rotate rightward.")
                        rightwardAnimator.start()
                    } else {
                        Log.i("RotatedScribbleView", "User flinged to rotate leftward.")
                        leftwardAnimator.start()
                    }
                } else {
                    if (upward > 0) {
                        upwardAnimator.start()
                    } else {
                        downwardAnimator.start()
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}