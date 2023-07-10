package com.cormontia.android.rotateyourscribble

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
        private set;
    //TODO?~ Replace these two with PointF
    var centerX: Double
        private set;
    var centerY: Double
        private set;

    init {
        val contentWidth = width - paddingLeft - paddingRight
        centerX = (contentWidth / 2).toDouble()

        val contentHeight = height - paddingTop - paddingBottom
        centerY = (contentHeight / 2).toDouble()

        val left = paddingLeft
        val top = paddingTop
        val right = left + contentWidth
        val bottom = top + contentHeight

        rect = Rect(left, top, right, bottom)
    }
}