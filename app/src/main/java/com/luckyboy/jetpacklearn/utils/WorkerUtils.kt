@file:JvmName("WorkerUtils")
package com.luckyboy.jetpacklearn.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.common.BaseConstant.CHANNEL_ID
import com.luckyboy.jetpacklearn.common.BaseConstant.DELAY_TIME_MILLS
import com.luckyboy.jetpacklearn.common.BaseConstant.NOTIFICATION_ID
import com.luckyboy.jetpacklearn.common.BaseConstant.NOTIFICATION_TITLE
import com.luckyboy.jetpacklearn.common.BaseConstant.OUTPUT_PATH
import com.luckyboy.jetpacklearn.common.BaseConstant.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.luckyboy.jetpacklearn.common.BaseConstant.VERBOSE_NOTIFICATION_CHANNEL_NAME
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

fun makeStatusNotificaiton(message: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.common_ic_account)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

}


fun sleep() {
    try {
        Thread.sleep(DELAY_TIME_MILLS, 0)
    } catch (e: InterruptedException) {
        Log.e("WorkUtils", e.message)
    }
}


@WorkerThread
fun blurBitmap(bitmap: Bitmap, applicationContext: Context): Bitmap? {
    lateinit var rsContext: RenderScript
    try {
        // create the output bitmap
        val output: Bitmap = Bitmap.createBitmap(
            bitmap.width, bitmap.height, bitmap.config
        )
        // Blur the image
        rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
        val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
        val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
        val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        theIntrinsic.apply {
            setRadius(10f)
            theIntrinsic.setInput(inAlloc)
            theIntrinsic.forEach(outAlloc)
        }
        outAlloc.copyTo(output)

        return output
    } finally {
        rsContext.finish()
    }
}


fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap?): Uri? {
    val name = String.format("blue-filter-output-%s.png", UUID.randomUUID())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()){
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, name)
    var out:FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore:IOException){

            }
        }
    }
    return Uri.fromFile(outputFile);
}


