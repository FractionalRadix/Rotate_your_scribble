package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val viewModel: ScribbleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rotatedScribbleView = findViewById<RotatedScribbleView>(R.id.rotatedScribbleview)

        viewModel.points.observe(this) {
                list -> rotatedScribbleView.setPoints(list)
                //TODO?~ Put this in an observer of its own? It needs to be done only once. Maybe not even something for an observer!
                rotatedScribbleView.setCenter(viewModel.centerX, viewModel.centerY)
        }
    }

    fun accept(points: MutableList<PointF>) {
        viewModel.accept(points)
    }

    fun setCenter(centerX: Double, centerY: Double) {
        viewModel.setCenter(centerX, centerY)
    }
}