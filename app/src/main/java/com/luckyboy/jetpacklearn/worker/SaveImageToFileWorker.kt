package com.luckyboy.jetpacklearn.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.luckyboy.jetpacklearn.common.BaseConstant.KEY_IMAGE_URI
import com.luckyboy.jetpacklearn.utils.makeStatusNotificaiton
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

// 存储照片的Worker
class SaveImageToFileWorker(ctx: Context, parameters: WorkerParameters):Worker(ctx, parameters){

    private val TAG by lazy {
        this::class.java.simpleName
    }

    private val title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
         Locale.getDefault()
    )

    override fun doWork(): Result {
       Log.e(TAG, "保存图片文件操作....")
       makeStatusNotificaiton("Saving image", applicationContext)
       val resolver = applicationContext.contentResolver
       return try {
           // 获取从外部传入的参数
           val resourceUri = inputData.getString(KEY_IMAGE_URI)
           var bitmap = BitmapFactory.decodeStream(
               resolver.openInputStream(Uri.parse(resourceUri))
           )
           var imageUrl = MediaStore.Images.Media.insertImage(
               resolver,
               bitmap,
               title,
               dateFormatter.format(Date())
           )
           if (!imageUrl.isNullOrEmpty()){
               Log.e(TAG, "保存到数据库中图片文件是 imageUrl $imageUrl")
               val output = workDataOf(KEY_IMAGE_URI to imageUrl)
               Result.success(output)
           }else{
               Log.e(TAG, "Writing to MediaStore failed")
               Result.failure()
           }
       } catch (exception: Exception) {
           Log.e(TAG, "Unable to save image to Gallery ", exception)
           Result.failure()
       }
    }



}