package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cormontia.android.rotateyourscribble.matrix3d.MatrixFactory
import com.cormontia.android.rotateyourscribble.matrix3d.Vec4

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


        /*
        //TODO!- This should come from the ViewModel.
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val contentWidth = width - paddingLeft - paddingRight
        centerX = (contentWidth / 2).toDouble()

        //TODO!- This should come from the ViewModel.
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val contentHeight = height - paddingTop - paddingBottom
        centerY = (contentHeight / 2).toDouble()
         */


        //Log.i("RotatedScribbleView", "${paddingTop}/${contentHeight}/${paddingBottom} centerY==${centerY}")
        //Log.i("RotatedScribbleView", "${paddingLeft}/${contentWidth}/${paddingRight} centerX==${centerX}")


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
            val fullMatrix = translateAfter * rotation * translateBefore

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
}