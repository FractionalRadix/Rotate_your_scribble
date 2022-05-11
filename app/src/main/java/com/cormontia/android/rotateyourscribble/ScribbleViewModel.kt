package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScribbleViewModel : ViewModel() {

    private val points_backingField = mutableListOf<PointF>()
    //TODO?~ Use LiveData instead of MutableLiveData, so that external parties can't update this field?
    var points: MutableLiveData<MutableList<PointF>> = MutableLiveData()
    init {
        points.value = points_backingField
    }

    var centerX = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set;

    var centerY = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set;

    fun accept(newPoints: MutableList<PointF>) {
        points_backingField.addAll(newPoints)
        points.value = points_backingField
    }

    fun setCenter(centerX: Double, centerY: Double) {
        this.centerX = centerX
        this.centerY = centerY
    }

    fun clear() {
        points_backingField.clear()
        points.value = points_backingField
    }
}