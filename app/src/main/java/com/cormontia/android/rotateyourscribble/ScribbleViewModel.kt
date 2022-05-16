package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cormontia.android.rotateyourscribble.matrix3d.MatrixFactory
import com.cormontia.android.rotateyourscribble.matrix3d.Vec4

class ScribbleViewModel : ViewModel() {

    //TODO?- Is the backing field still needed?
    private val points_backingField = mutableListOf<PointF>()
    //TODO?~ Use LiveData instead of MutableLiveData, so that external parties can't update this field?
    var points: MutableLiveData<MutableList<PointF>> = MutableLiveData()
    init {
        points.value = points_backingField
    }

    //TODO?~ Should this be LiveData too?
    var threeDimensionalModel = listOf<List<Vec4>>()

    var centerX = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set

    var centerY = 0.0 // Placeholder value, since primitive types cannot use "lateinit".
        private set

    //TODO?~ use List<PointF> as parameter, instead of MutableList<PointF> ?
    fun accept(newPoints: MutableList<PointF>) {
        points_backingField.addAll(newPoints)
        threeDimensionalModel = rotateScribble(points_backingField)
        points.value = points_backingField
    }

    fun setCenter(centerX: Double, centerY: Double) {
        this.centerX = centerX
        this.centerY = centerY
    }

    fun clear() {
        points_backingField.clear()
        threeDimensionalModel = rotateScribble(points_backingField)
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
     * Turn a scribble into a 3D model, by rotating it around the center.
     */
    private fun rotateScribble(points: List<PointF>): List<List<Vec4>> {
        val rotatedLines = mutableListOf<MutableList<Vec4>>()

        //TODO!~ Make these two center values independent, because this method is going to be moved to the ViewModel!
        val translateBefore = MatrixFactory.Translate(-centerX, -centerY, 0.0)
        val translateAfter = MatrixFactory.Translate(+centerX, +centerY, 0.0)

        for (angleInDegrees in 0 until 360 step 5) {
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
            val rotation = MatrixFactory.RotateAroundY(angleInRadians)

            val fullMatrix = translateAfter * rotation * translateBefore

            val rotatedLine = mutableListOf<Vec4>()
            for (point in points) {
                val pointAsVector = Vec4(point.x.toDouble(), point.y.toDouble(), 0.0, 1.0)
                val rotatedPointAsVector = fullMatrix.multiply(pointAsVector)
                rotatedLine.add(rotatedPointAsVector)
            }

            rotatedLines.add(rotatedLine)
        }
        return rotatedLines
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