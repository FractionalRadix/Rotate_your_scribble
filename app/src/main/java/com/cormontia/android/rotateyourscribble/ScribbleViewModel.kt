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

    fun accept(newPoints: MutableList<PointF>) {
        points_backingField.addAll(newPoints)
        points.value = points_backingField
    }
}