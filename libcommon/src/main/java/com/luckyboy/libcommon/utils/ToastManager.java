package com.luckyboy.libcommon.utils;

import android.os.Looper;
import android.widget.Toast;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.luckyboy.libcommon.global.AppGlobals;

public class ToastManager {


    public static void showToast(String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(AppGlobals.getInstance(), message, Toast.LENGTH_SHORT).show();
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                Toast.makeText(AppGlobals.getInstance(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }

}
