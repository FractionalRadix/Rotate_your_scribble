package com.cormontia.android.rotateyourscribble

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import java.io.*
import kotlin.streams.toList

interface PointsReceiver {
    fun accept(points: MutableList<PointF>)
    fun setCenter(center: PointF)
}

class MainActivity : AppCompatActivity(), PointsReceiver {

    companion object {
        const val storageFileMimeType = "text/plain"
    }

    private val viewModel: ScribbleViewModel by viewModels()

    private val loadLauncher = registerForActivityResult(LoaderContract()) { uri -> load(uri) }
    private val saveLauncher = registerForActivityResult(SaverContract()) { uri -> save(uri) }
    private val exportLauncher = registerForActivityResult(ExporterContract()) { uri -> export(uri) }

    //TODO?~ See if we can have a launcher that does NOT require input (0 parameters instead of a dummy String parameter).
    //TODO?~ Use the default GetContent or OpenDocument contract?
    class LoaderContract: ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            val loadIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            loadIntent.addCategory(Intent.CATEGORY_OPENABLE)
            loadIntent.type = storageFileMimeType
            return loadIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    //TODO?~ See if we can have a launcher that does NOT require input (0 parameters instead of a dummy String parameter).
    //TODO?~ Use the CreateDocument contract?
    class SaverContract: ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
            //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
            // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
            val saveIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            saveIntent.type = storageFileMimeType
            saveIntent.putExtra(Intent.EXTRA_TITLE, "scribble.txt") //TODO?~ Add timestamp or something to make it unique?
            return saveIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    //TODO?~ See if we can have a launcher that does NOT require input (0 parameters instead of a dummy String parameter).
    class ExporterContract : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
            //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
            // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
            val exportIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            exportIntent.type = storageFileMimeType
            exportIntent.putExtra(Intent.EXTRA_TITLE, "scribble.obj") //TODO?~ Add timestamp or something to make it unique?
            return exportIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

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
        findViewById<ImageButton>(R.id.shareButton).setOnClickListener{ share() }
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

    private val shareCode = 35
    private fun share() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"   // That apparently really IS the MIME Type for Wavefront.OBJ ....
        val textLines = viewModel
            .toWavefrontFormat()
            .joinToString( separator = "\r\n" )
        //TODO?~ The amount of data in even a simple scribble, causes the thing to crash... looks like a timeout.
        sharingIntent.putExtra(Intent.EXTRA_TEXT, textLines)
        startActivity(Intent.createChooser(sharingIntent, "Share using: "))
    }

    private fun load(uri: Uri?) {
        if (uri != null) {
            //TODO!+ Handle FileNotFoundException and IOException.
            //TODO!+ Handle parsing errors.
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
}