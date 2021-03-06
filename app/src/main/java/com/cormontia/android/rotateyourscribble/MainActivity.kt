package com.cormontia.android.rotateyourscribble

import android.content.Intent
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import java.io.*
import kotlin.streams.toList

class MainActivity : AppCompatActivity() {

    private val viewModel: ScribbleViewModel by viewModels()

    private val storageFileMimeType = "text/plain"

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

                rotatedScribbleView.setCenter(viewModel.centerX, viewModel.centerY)

                rotatedScribbleView.set3DModel(viewModel.threeDimensionalModel)
            }
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
        viewModel.clear()
    }

    private val loadCode = 14
    private fun load() {
        val loadIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadIntent.type = storageFileMimeType
        startActivityForResult(loadIntent, loadCode) //TODO!~ Use "registerForActivityResult" instead.
    }

    private val saveCode = 21
    private fun save() {
        // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
        //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
        // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
        val saveIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        saveIntent.type = storageFileMimeType
        saveIntent.putExtra(Intent.EXTRA_TITLE, "scribble.txt") //TODO?~ Add timestamp or something to make it unique?
        startActivityForResult(saveIntent, saveCode) //TODO!~ Use "registerForActivityResult" instead.
    }

    private val exportCode = 28
    private fun export() {
        // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
        //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
        // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
        val exportIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        exportIntent.type = storageFileMimeType
        exportIntent.putExtra(Intent.EXTRA_TITLE, "scribble.obj") //TODO?~ Add timestamp or something to make it unique?
        startActivityForResult(exportIntent, exportCode) //TODO!~ Use "registerForActivityResult" instead.
    }

    private val shareCode = 35
    private fun share() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"   // That apparently really IS the MIME Type for Wavefront.OBJ ....
        val textLines = viewModel
            .toWavefrontFormat()
            .joinToString( separator = "\r\n" )
        //TODO?~ The amount of data in even a simple scribble, causes the thing to crash... looks like a timeout.
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textLines)
        startActivity(Intent.createChooser(sharingIntent, "Share using: "))
    }

    @Deprecated("'onActivityResult(Int, Int, Intent?): Unit' is deprecated. Overrides deprecated member in 'androidx.activity.ComponentActivity'. Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        val uri = resultData?.data
        when (requestCode) {
            loadCode -> {
                if (uri != null) {
                    //TODO!+ Handle FileNotFoundException and IOException
                    val contentResolver = applicationContext.contentResolver
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                    val fileReader = FileReader(parcelFileDescriptor?.fileDescriptor)
                    val stringList = BufferedReader(fileReader)
                        .lines()
                        .filter { !it.isNullOrBlank() }
                        .toList()
                    val points = viewModel.deserializePointsList(stringList)
                    viewModel.clear()
                    viewModel.accept(points as MutableList<PointF>)
                }
            }
            saveCode -> {
                if (uri != null) {
                    //TODO!+ Handle FileNotFoundException and IOException
                    val contentResolver = applicationContext.contentResolver
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
                    val fileWriter = FileWriter(parcelFileDescriptor?.fileDescriptor)
                    val bufferedWriter = BufferedWriter(fileWriter)

                    bufferedWriter.use {
                        val pointsAsStrings = viewModel.serializePointsList()
                        pointsAsStrings.forEach {
                            line -> bufferedWriter.appendLine(line)
                        }
                    }
                }
            }
            exportCode -> {
                if (uri != null) {
                    //TODO!+ Handle FileNotFoundException and IOException
                    val contentResolver = applicationContext.contentResolver
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
                    val fileWriter = FileWriter(parcelFileDescriptor?.fileDescriptor)
                    val bufferedWriter = BufferedWriter(fileWriter)

                    val textLines = viewModel.toWavefrontFormat()
                    bufferedWriter.use {
                        //TODO!~ Currently stores as .obj.txt ...
                        textLines.forEach {
                            line -> bufferedWriter.appendLine(line)
                        }
                    }
                }
            }
        }
    }
}