package com.cormontia.android.rotateyourscribble.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class SharerContract : ActivityResultContract<List<String>, Uri?>() {
    override fun createIntent(context: Context, input: List<String>): Intent {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"   // That apparently really IS the MIME Type for Wavefront.OBJ ....
        val textLines = input
            .joinToString( separator = "\r\n" )
        //TODO?~ The amount of data in even a simple scribble, causes the thing to crash:
        //   Caused by: android.os.TransactionTooLargeException: data parcel size 2098740 bytes
        sharingIntent.putExtra(Intent.EXTRA_TEXT, textLines)
        return sharingIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }

}