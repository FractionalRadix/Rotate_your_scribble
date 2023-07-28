package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import android.graphics.Rect

/**
 * Auxiliary class to cache calculated data about Views.
 * Given the width, height, and padding of a view, the constructor will calculate the view's bounding rectangle and center.
 * These values are then available without needing re-calculation.
 */
class ViewInfo(
    width: Int,
    height: Int,
    paddingLeft: Int,
    paddingTop: Int,
    paddingRight: Int,
    paddingBottom: Int,
) {
    var rect: Rect
        private set
    var center: PointF
        private set
    val contentWidth: Int
        get() = rect.right - rect.left
    val contentHeight: Int
        get() = rect.bottom - rect.top

    init {
        val contentWidth = width - paddingLeft - paddingRight
        val centerX = (contentWidth / 2).toFloat()

        val contentHeight = height - paddingTop - paddingBottom
        val centerY = (contentHeight / 2).toFloat()

        center = PointF(centerX, centerY)

        val left = paddingLeft
        val top = paddingTop
        val right = left + contentWidth
        val bottom = top + contentHeight

        rect = Rect(left, top, right, bottom)
    }
}