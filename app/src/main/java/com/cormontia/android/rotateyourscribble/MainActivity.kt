package com.cormontia.android.rotateyourscribble

import android.graphics.PointF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import com.cormontia.android.rotateyourscribble.contracts.ExporterContract
import com.cormontia.android.rotateyourscribble.contracts.LoaderContract
import com.cormontia.android.rotateyourscribble.contracts.SaverContract
import com.cormontia.android.rotateyourscribble.contracts.SharerContract
import java.io.*
import kotlin.streams.toList

class MainActivity : AppCompatActivity(), PointsReceiver {

    companion object {
        const val storageFileMimeType = "text/plain"
    }

    private val viewModel: ScribbleViewModel by viewModels()

    private val loadLauncher = registerForActivityResult(LoaderContract()) { uri -> load(uri) }
    private val saveLauncher = registerForActivityResult(SaverContract()) { uri -> save(uri) }
    private val exportLauncher = registerForActivityResult(ExporterContract()) { uri -> export(uri) }
    private val shareLauncher = registerForActivityResult(SharerContract()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rotatedScribbleView = findViewById<RotatedScribbleView>(R.id.rotatedScribbleview)
        val flatScribbleView = findViewById<FlatScribbleView>(R.id.flatScribbleView)

        viewModel.points.observe(this) {
            list -> run {
                flatScribbleView.setPoints(list)

                //TODO?~ Put this in an observer of its own? It needs to be done only once. Maybe not even something for an observer!
                // Also, it seems that (centerX, centerY)==(0.0, 0.0) when the first thing you do is load a model.
                // This happens because these values are calculated in the onDraw() method of FlatScribbleView, which is not yet called at that point.

                rotatedScribbleView.setCenter(viewModel.center)
                rotatedScribbleView.set3DModel(viewModel.threeDimensionalModel)
            }
        }

        findViewById<ImageButton>(R.id.clearButton).setOnClickListener{ clear() }
        findViewById<ImageButton>(R.id.loadButton).setOnClickListener{ loadLauncher.launch("Dummy") }
        findViewById<ImageButton>(R.id.saveButton).setOnClickListener{ saveLauncher.launch("Dummy") }
        findViewById<ImageButton>(R.id.exportButton).setOnClickListener{ exportLauncher.launch("Dummy") }
        //TODO!~ Right now, we only send the first 10 lines. Sharing may not be appropriate for the amount of data involved. Maybe send a binary instead of WaveFront.
        findViewById<ImageButton>(R.id.shareButton).setOnClickListener{ shareLauncher.launch(viewModel.toWavefrontFormat().take(10)) }
    }

    override fun accept(points: MutableList<PointF>) {
        viewModel.accept(points)
    }

    override fun setCenter(center: PointF) {
        viewModel.setCenter(center)
    }

    private fun clear() {
        viewModel.clear()
    }

    private fun load(uri: Uri?) {
        if (uri != null) {
            //TODO!+ Handle FileNotFoundException and IOException.
            val contentResolver = applicationContext.contentResolver
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileReader = FileReader(parcelFileDescriptor?.fileDescriptor)
            val stringList = BufferedReader(fileReader)
                .lines()
                .filter { !it.isNullOrBlank() }
                .toList()
            parcelFileDescriptor?.close()
            val parseResult = viewModel.deserializePointsList(stringList)
            val points = parseResult.first
            val nrOfParseErrors = parseResult.second
            if (nrOfParseErrors > 0) {
                val errToast = Toast.makeText(this, "Errors trying to load. File may be corrupted or not in the right format.", Toast.LENGTH_LONG)
                errToast.show()
            }
            viewModel.clear()
            viewModel.accept(points as MutableList<PointF>)
        }
    }

    private fun save(uri: Uri?) {
        if (uri != null) {
            //TODO!+ Handle FileNotFoundException and IOException.
            val contentResolver = applicationContext.contentResolver
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            val fileWriter = FileWriter(parcelFileDescriptor?.fileDescriptor)
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.use {
                val pointsAsStrings = viewModel.serializePointsList()
                pointsAsStrings.forEach { line ->
                    bufferedWriter.appendLine(line)
                }
            }

            parcelFileDescriptor?.close()
        }
    }

    private fun export(uri: Uri?) {
        if (uri != null) {
            //TODO!+ Handle FileNotFoundException and IOException
            val contentResolver = applicationContext.contentResolver
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            val fileWriter = FileWriter(parcelFileDescriptor?.fileDescriptor)
            val bufferedWriter = BufferedWriter(fileWriter)

            val textLines = viewModel.toWavefrontFormat()
            bufferedWriter.use {
                //TODO!~ Currently stores as .obj.txt ...
                textLines.forEach { line ->
                    bufferedWriter.appendLine(line)
                }
            }

            parcelFileDescriptor?.close()
        }
    }

    /**
     * Given a
     */
    fun <T> Iterable<T>.filterSubsequentDoubles() {
        val l = listOf<T>()
        val l2 = l.take(3)
        val l3 = l.filter { x -> true }
    }

    //TODO!+ Add unit tests.
    fun removeDoubles(l: List<String>): List<String> {
        var result = mutableListOf<String>()

        if (l.isEmpty()) {
            return result
        }

        if (l.size == 1) {
            result.add(l[0])
            return result
        }

        var curVal = l[0]
        for (idx in 1..l.size) {
            var nextVal = l[idx]
            if (nextVal != curVal) {
                result.add(nextVal)
                curVal = nextVal
            }
        }

        return result
    }
}