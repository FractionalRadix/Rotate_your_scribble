package com.cormontia.android.rotateyourscribble.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.cormontia.android.rotateyourscribble.MainActivity

//TODO?~ See if we can have a launcher that does NOT require input (0 parameters instead of a dummy String parameter).
//TODO?~ Use the CreateDocument contract?
class SaverContract: ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
        //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
        // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
        val saveIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        saveIntent.type = MainActivity.storageFileMimeType
        saveIntent.putExtra(Intent.EXTRA_TITLE, "scribble.txt") //TODO?~ Add timestamp or something to make it unique?
        return saveIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}