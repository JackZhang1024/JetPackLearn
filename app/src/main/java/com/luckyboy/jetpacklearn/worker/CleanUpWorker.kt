package com.luckyboy.jetpacklearn.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.luckyboy.jetpacklearn.common.BaseConstant.OUTPUT_PATH
import com.luckyboy.jetpacklearn.utils.makeStatusNotificaiton
import java.io.File
import java.lang.Exception

// 清理临时文件的Worker
class CleanUpWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val TAG by lazy {
        this::class.java.simpleName
    }

    override fun doWork(): Result {
        makeStatusNotificaiton("Cleaning up old temporary files", applicationContext)

        return try {
            // 删除逻辑
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()){
                val entries = outputDirectory.listFiles()
                if (entries!=null){
                    for (entry in entries){
                         val name = entry.name
                         if (name.isNotEmpty() && name.endsWith(".png")){
                             val deleted = entry.delete()
                             Log.e(TAG, String.format("Deleted %s - %s", name, deleted))
                         }
                    }
                }
            }
            // 成功时返回
            Result.success()
        } catch (exception: Exception) {
            // 失败时返回
            Result.failure()
        }
    }

}