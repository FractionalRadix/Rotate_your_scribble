package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScribbleViewModel : ViewModel() {

    //TODO?- Is the backing field still needed?
    private val points_backingField = mutableListOf<PointF>()
    //TODO?~ Use LiveData instead of MutableLiveData, so that external parties can't update this field?
    var points: MutableLiveData<MutableList<PointF>> = MutableLiveData()
    init {
        points.value = points_backingField
    }

    var centerX = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set

    var centerY = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set

    //TODO?~ use List<PointF> as parameter, instead of MutableList<PointF> ?
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

    //TODO?~ Make this thread-safe? Is it possible for the points to be changed DURING serialization?
    fun serializePointsList(): List<String> {
        val serialization = mutableListOf<String>()
        for (point in points_backingField) {
            serialization.add("${point.x} ${point.y}")
        }
        return serialization
    }

    //TODO?~ Make this static (i.e. put it in a companion object?) Note that "serialize" should NOT be static!
    fun deserializePointsList(stringList: List<String>): List<PointF> {
        val points = mutableListOf<PointF>()
        for (point in stringList) {
            val split = point.split(' ')
            if (split.size != 2) {
                //TODO!+ Issue an error
            } else {
                val x = split[0].toFloat()
                val y = split[1].toFloat()
                points.add(PointF(x,y))
            }
        }
        return points
    }

    /**
     * Transform the 3D figure to the WaveFront .OBJ format.
     */
    fun toWavefrontFormat(): List<String> {
        val wavefront = mutableListOf<String>()
        //TODO!+
        return wavefront
    }
}