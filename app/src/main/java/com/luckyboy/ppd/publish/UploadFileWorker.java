package com.luckyboy.ppd.publish;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.luckyboy.libcommon.utils.FileUploadManager;

public class UploadFileWorker extends Worker {

    private static final String TAG = "UploadFileWorker";

    public UploadFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String filePath = inputData.getString("file");
        String fileUrl = FileUploadManager.upload(filePath);
        Log.e(TAG, "doWork: 上传阿里云存储服务器图片或者视频地址 " + fileUrl);
        if (TextUtils.isEmpty(fileUrl)) {
            return Result.failure();
        } else {
            Data outputData = new Data.Builder()
                    .putString("fileUrl", fileUrl)
                    .build();
            return Result.success(outputData);
        }
    }

}
