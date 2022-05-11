package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val viewModel: ScribbleViewModel by viewModels()

    private lateinit var flatScribbleView : FlatScribbleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rotatedScribbleView = findViewById<RotatedScribbleView>(R.id.rotatedScribbleview)
        flatScribbleView = findViewById<FlatScribbleView>(R.id.flatScribbleView)

        viewModel.points.observe(this) {
                list -> rotatedScribbleView.setPoints(list)
                //TODO?~ Put this in an observer of its own? It needs to be done only once. Maybe not even something for an observer!
                rotatedScribbleView.setCenter(viewModel.centerX, viewModel.centerY)
        }

        findViewById<ImageButton>(R.id.clearButton).setOnClickListener{ clear() }
        findViewById<ImageButton>(R.id.loadButton).setOnClickListener{ load() }
        findViewById<ImageButton>(R.id.saveButton).setOnClickListener{ save() }
        findViewById<ImageButton>(R.id.exportButton).setOnClickListener{ export() }
        findViewById<ImageButton>(R.id.shareButton).setOnClickListener{ share() }
    }

    fun accept(points: MutableList<PointF>) {
        viewModel.accept(points)
    }

    fun setCenter(centerX: Double, centerY: Double) {
        viewModel.setCenter(centerX, centerY)
    }

    private fun clear() {
        Log.i("MainActivity", "Hit Clear button.")
        viewModel.clear()
        //TODO!~ FlatScribbleView should be updated from the ViewModel after all...
        flatScribbleView.clear()
    }

    private fun load() {
        //TODO()
    }

    private fun save() {
        //TODO()
    }

    private fun export() {
        //TODO()
    }

    private fun share() {
        //TODO()
    }
}