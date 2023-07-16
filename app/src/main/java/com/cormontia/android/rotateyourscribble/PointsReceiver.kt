package com.cormontia.android.rotateyourscribble

import android.graphics.PointF

interface PointsReceiver {
    fun accept(points: MutableList<PointF>)
    fun setCenter(center: PointF)
}