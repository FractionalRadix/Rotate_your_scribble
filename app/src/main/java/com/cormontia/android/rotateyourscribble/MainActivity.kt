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

        //TODO?~ Check if these variables need to be members instead of values local to `onCreate`.
        val flatScribbleView = findViewById<FlatScribbleView>(R.id.flatScribbleView)
        val rotatedScribbleView = findViewById<RotatedScribbleView>(R.id.rotatedScribbleview)

        viewModel.points3.observe(this) { list ->
            flatScribbleView.setPoints(list);
            rotatedScribbleView.setPoints(list)
        }
    }

    fun accept(points: MutableList<PointF>) {
        Log.i("MainActivity", "Sending points to ViewModel: $points")
        viewModel.accept(points)
    }
}