package com.luckyboy.jetpacklearn.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.luckyboy.jetpacklearn.common.BaseConstant.KEY_IMAGE_URI
import com.luckyboy.jetpacklearn.utils.blurBitmap
import com.luckyboy.jetpacklearn.utils.makeStatusNotificaiton
import com.luckyboy.jetpacklearn.utils.writeBitmapToFile

// 模糊处理的Worker
class BlurWorker (context: Context, params:WorkerParameters):Worker(context, params){

    private var TAG:String = this::class.java.simpleName

    override fun doWork(): Result {
        val context = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        // 通知开始处理图片
        makeStatusNotificaiton("Blurring image", context)

        return try {
            // 图片处理逻辑
            if (TextUtils.isEmpty(resourceUri)){
               Log.e(TAG, "Invalid input uri")
               throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = context.contentResolver
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            // 创建Bitmap 文件
            val output = blurBitmap(picture, context)
            // 存入路径
            val outputUri = writeBitmapToFile(context, output)
            // 输出路径
            val outPutData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            makeStatusNotificaiton("Output is $outputUri", context)
            Log.e(TAG, "output file is $outputUri")
            Result.success(outPutData)
        } catch (throwable:Throwable){
            Result.failure()
        }
    }


}