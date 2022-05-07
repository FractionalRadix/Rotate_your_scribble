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

        viewModel.points.observe(this) { list ->
            rotatedScribbleView.setPoints(list)
        }
    }

    fun accept(points: MutableList<PointF>) {
        viewModel.accept(points)
    }
}