package com.luckyboy.libcommon.utils;

import android.util.DisplayMetrics;

import com.luckyboy.libcommon.global.AppGlobals;

public class PixUtils {

    public static int dp2px(int dpValue) {
        DisplayMetrics metrics = AppGlobals.getInstance().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = AppGlobals.getInstance().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = AppGlobals.getInstance().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
