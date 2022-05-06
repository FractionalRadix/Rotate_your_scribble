package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScribbleViewModel : ViewModel() {

    //TODO!- Replace with LiveData.
    private val points = mutableListOf<PointF>()

    //TODO!- This was just trying out how it worked.
    val points1 : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    //TODO!- This was just trying out how it worked.
    val points2 : LiveData<String> by lazy {
        MutableLiveData()
    }
    //TODO!- Rename to just "points", once we got the LiveData working properly.
    val points3 : MutableLiveData<MutableList<PointF>> by lazy {
        MutableLiveData()
    }

    //TODO?~ Check if the setter for this is correct.
    private var _points4 = MutableLiveData<MutableList<PointF>>()
    var points4: LiveData<MutableList<PointF>>
        get() = _points4
        set(newValue) {
            _points4.value = newValue.value
        }


    fun accept(newPoints: MutableList<PointF>) {
        points.addAll(newPoints) //TODO!- We are going to use the LiveData.

        // Add the new points to the LiveData.
        /*
        val currentPoints = points3.value
        if (currentPoints == null) {
            Log.e("Scribble ViewModel", "points3 is null.")
        } else {
            currentPoints.addAll(newPoints)
            points3.value = currentPoints
        }
         */

        if (points4 == null) {
            Log.e("Scribble ViewModel", "points4 is null")
        } else {
            val currentPoints = points4.value
            if (currentPoints == null) {
                Log.e("Scribble ViewModel", "points4.value is null")
            } else {
                //TODO!+
            }

        }

    }
}