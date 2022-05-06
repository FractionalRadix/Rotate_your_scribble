package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class RotatedScribbleView : View {

    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...


    /**
     * The font color
     */
    var exampleColor: Int
        get() = _exampleColor
        set(value) {
            _exampleColor = value
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var exampleDimension: Float
        get() = _exampleDimension
        set(value) {
            _exampleDimension = value
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

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

        _exampleColor = a.getColor(
            R.styleable.RotatedScribbleView_exampleColor2,
            exampleColor
        )
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        _exampleDimension = a.getDimension(
            R.styleable.RotatedScribbleView_exampleDimension2,
            exampleDimension
        )

        if (a.hasValue(R.styleable.RotatedScribbleView_exampleDrawable2)) {
            exampleDrawable = a.getDrawable(
                R.styleable.RotatedScribbleView_exampleDrawable2
            )
            exampleDrawable?.callback = this
        }

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

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(
                paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight
            )
            it.draw(canvas)
        }

        val greenPaint = Paint()
        greenPaint.color = Color.GREEN
        greenPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle(40f, 40f, 30f, greenPaint)

    }
}